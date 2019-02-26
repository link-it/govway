/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.details.model;

import org.openspcoop2.core.eccezione.details.DettaglioEccezione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DettaglioEccezione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DettaglioEccezioneModel extends AbstractModel<DettaglioEccezione> {

	public DettaglioEccezioneModel(){
	
		super();
	
		this.DOMAIN = new org.openspcoop2.core.eccezione.details.model.DominioModel(new Field("domain",org.openspcoop2.core.eccezione.details.Dominio.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.TIMESTAMP = new Field("timestamp",java.util.Date.class,"dettaglio-eccezione",DettaglioEccezione.class);
		this.EXCEPTIONS = new org.openspcoop2.core.eccezione.details.model.EccezioniModel(new Field("exceptions",org.openspcoop2.core.eccezione.details.Eccezioni.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.DETAILS = new org.openspcoop2.core.eccezione.details.model.DettagliModel(new Field("details",org.openspcoop2.core.eccezione.details.Dettagli.class,"dettaglio-eccezione",DettaglioEccezione.class));
	
	}
	
	public DettaglioEccezioneModel(IField father){
	
		super(father);
	
		this.DOMAIN = new org.openspcoop2.core.eccezione.details.model.DominioModel(new ComplexField(father,"domain",org.openspcoop2.core.eccezione.details.Dominio.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.TIMESTAMP = new ComplexField(father,"timestamp",java.util.Date.class,"dettaglio-eccezione",DettaglioEccezione.class);
		this.EXCEPTIONS = new org.openspcoop2.core.eccezione.details.model.EccezioniModel(new ComplexField(father,"exceptions",org.openspcoop2.core.eccezione.details.Eccezioni.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.DETAILS = new org.openspcoop2.core.eccezione.details.model.DettagliModel(new ComplexField(father,"details",org.openspcoop2.core.eccezione.details.Dettagli.class,"dettaglio-eccezione",DettaglioEccezione.class));
	
	}
	
	

	public org.openspcoop2.core.eccezione.details.model.DominioModel DOMAIN = null;
	 
	public IField TIMESTAMP = null;
	 
	public org.openspcoop2.core.eccezione.details.model.EccezioniModel EXCEPTIONS = null;
	 
	public org.openspcoop2.core.eccezione.details.model.DettagliModel DETAILS = null;
	 

	@Override
	public Class<DettaglioEccezione> getModeledClass(){
		return DettaglioEccezione.class;
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