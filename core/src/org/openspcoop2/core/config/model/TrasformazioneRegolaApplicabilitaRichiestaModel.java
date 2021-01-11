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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRegolaApplicabilitaRichiesta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRegolaApplicabilitaRichiestaModel extends AbstractModel<TrasformazioneRegolaApplicabilitaRichiesta> {

	public TrasformazioneRegolaApplicabilitaRichiestaModel(){
	
		super();
	
		this.AZIONE = new Field("azione",java.lang.String.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class);
		this.PATTERN = new Field("pattern",java.lang.String.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class);
		this.SOGGETTO = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaSoggettoModel(new Field("soggetto",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class));
	
	}
	
	public TrasformazioneRegolaApplicabilitaRichiestaModel(IField father){
	
		super(father);
	
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class);
		this.PATTERN = new ComplexField(father,"pattern",java.lang.String.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class);
		this.SOGGETTO = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaSoggettoModel(new ComplexField(father,"soggetto",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo.class,"trasformazione-regola-applicabilita-richiesta",TrasformazioneRegolaApplicabilitaRichiesta.class));
	
	}
	
	

	public IField AZIONE = null;
	 
	public IField CONTENT_TYPE = null;
	 
	public IField PATTERN = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaSoggettoModel SOGGETTO = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 

	@Override
	public Class<TrasformazioneRegolaApplicabilitaRichiesta> getModeledClass(){
		return TrasformazioneRegolaApplicabilitaRichiesta.class;
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