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
	
		this.DOMAIN = new org.openspcoop2.core.eccezione.errore_applicativo.model.DominioModel(new Field("domain",org.openspcoop2.core.eccezione.errore_applicativo.Dominio.class,"errore-applicativo",ErroreApplicativo.class));
		this.TIMESTAMP = new Field("timestamp",java.util.Date.class,"errore-applicativo",ErroreApplicativo.class);
		this.SERVICE = new org.openspcoop2.core.eccezione.errore_applicativo.model.DatiCooperazioneModel(new Field("service",org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione.class,"errore-applicativo",ErroreApplicativo.class));
		this.EXCEPTION = new org.openspcoop2.core.eccezione.errore_applicativo.model.EccezioneModel(new Field("exception",org.openspcoop2.core.eccezione.errore_applicativo.Eccezione.class,"errore-applicativo",ErroreApplicativo.class));
	
	}
	
	public ErroreApplicativoModel(IField father){
	
		super(father);
	
		this.DOMAIN = new org.openspcoop2.core.eccezione.errore_applicativo.model.DominioModel(new ComplexField(father,"domain",org.openspcoop2.core.eccezione.errore_applicativo.Dominio.class,"errore-applicativo",ErroreApplicativo.class));
		this.TIMESTAMP = new ComplexField(father,"timestamp",java.util.Date.class,"errore-applicativo",ErroreApplicativo.class);
		this.SERVICE = new org.openspcoop2.core.eccezione.errore_applicativo.model.DatiCooperazioneModel(new ComplexField(father,"service",org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione.class,"errore-applicativo",ErroreApplicativo.class));
		this.EXCEPTION = new org.openspcoop2.core.eccezione.errore_applicativo.model.EccezioneModel(new ComplexField(father,"exception",org.openspcoop2.core.eccezione.errore_applicativo.Eccezione.class,"errore-applicativo",ErroreApplicativo.class));
	
	}
	
	

	public org.openspcoop2.core.eccezione.errore_applicativo.model.DominioModel DOMAIN = null;
	 
	public IField TIMESTAMP = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.DatiCooperazioneModel SERVICE = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.EccezioneModel EXCEPTION = null;
	 

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