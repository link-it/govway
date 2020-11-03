/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRisposta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ValidazioneContenutiApplicativiRisposta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneContenutiApplicativiRispostaModel extends AbstractModel<ValidazioneContenutiApplicativiRisposta> {

	public ValidazioneContenutiApplicativiRispostaModel(){
	
		super();
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiStatoModel(new Field("configurazione",org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class));
		this.APPLICABILITA = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiRispostaApplicabilitaModel(new Field("applicabilita",org.openspcoop2.core.config.ValidazioneContenutiApplicativiRispostaApplicabilita.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class));
		this.NOME = new Field("nome",java.lang.String.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class);
		this.POSIZIONE = new Field("posizione",int.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class);
	
	}
	
	public ValidazioneContenutiApplicativiRispostaModel(IField father){
	
		super(father);
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiStatoModel(new ComplexField(father,"configurazione",org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class));
		this.APPLICABILITA = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiRispostaApplicabilitaModel(new ComplexField(father,"applicabilita",org.openspcoop2.core.config.ValidazioneContenutiApplicativiRispostaApplicabilita.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class);
		this.POSIZIONE = new ComplexField(father,"posizione",int.class,"validazione-contenuti-applicativi-risposta",ValidazioneContenutiApplicativiRisposta.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiStatoModel CONFIGURAZIONE = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiRispostaApplicabilitaModel APPLICABILITA = null;
	 
	public IField NOME = null;
	 
	public IField POSIZIONE = null;
	 

	@Override
	public Class<ValidazioneContenutiApplicativiRisposta> getModeledClass(){
		return ValidazioneContenutiApplicativiRisposta.class;
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