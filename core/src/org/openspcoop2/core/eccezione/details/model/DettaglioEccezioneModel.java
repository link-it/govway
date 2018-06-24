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
	
		this.DOMINIO = new org.openspcoop2.core.eccezione.details.model.DominioModel(new Field("dominio",org.openspcoop2.core.eccezione.details.Dominio.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"dettaglio-eccezione",DettaglioEccezione.class);
		this.ECCEZIONI = new org.openspcoop2.core.eccezione.details.model.EccezioniModel(new Field("eccezioni",org.openspcoop2.core.eccezione.details.Eccezioni.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.DETTAGLI = new org.openspcoop2.core.eccezione.details.model.DettagliModel(new Field("dettagli",org.openspcoop2.core.eccezione.details.Dettagli.class,"dettaglio-eccezione",DettaglioEccezione.class));
	
	}
	
	public DettaglioEccezioneModel(IField father){
	
		super(father);
	
		this.DOMINIO = new org.openspcoop2.core.eccezione.details.model.DominioModel(new ComplexField(father,"dominio",org.openspcoop2.core.eccezione.details.Dominio.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"dettaglio-eccezione",DettaglioEccezione.class);
		this.ECCEZIONI = new org.openspcoop2.core.eccezione.details.model.EccezioniModel(new ComplexField(father,"eccezioni",org.openspcoop2.core.eccezione.details.Eccezioni.class,"dettaglio-eccezione",DettaglioEccezione.class));
		this.DETTAGLI = new org.openspcoop2.core.eccezione.details.model.DettagliModel(new ComplexField(father,"dettagli",org.openspcoop2.core.eccezione.details.Dettagli.class,"dettaglio-eccezione",DettaglioEccezione.class));
	
	}
	
	

	public org.openspcoop2.core.eccezione.details.model.DominioModel DOMINIO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public org.openspcoop2.core.eccezione.details.model.EccezioniModel ECCEZIONI = null;
	 
	public org.openspcoop2.core.eccezione.details.model.DettagliModel DETTAGLI = null;
	 

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