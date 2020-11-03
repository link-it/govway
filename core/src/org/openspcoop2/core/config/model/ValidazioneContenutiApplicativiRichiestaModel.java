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

import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiesta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ValidazioneContenutiApplicativiRichiesta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneContenutiApplicativiRichiestaModel extends AbstractModel<ValidazioneContenutiApplicativiRichiesta> {

	public ValidazioneContenutiApplicativiRichiestaModel(){
	
		super();
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiStatoModel(new Field("configurazione",org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class));
		this.APPLICABILITA = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiRichiestaApplicabilitaModel(new Field("applicabilita",org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiestaApplicabilita.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class));
		this.NOME = new Field("nome",java.lang.String.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class);
		this.POSIZIONE = new Field("posizione",int.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class);
	
	}
	
	public ValidazioneContenutiApplicativiRichiestaModel(IField father){
	
		super(father);
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiStatoModel(new ComplexField(father,"configurazione",org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class));
		this.APPLICABILITA = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiRichiestaApplicabilitaModel(new ComplexField(father,"applicabilita",org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiestaApplicabilita.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class);
		this.POSIZIONE = new ComplexField(father,"posizione",int.class,"validazione-contenuti-applicativi-richiesta",ValidazioneContenutiApplicativiRichiesta.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiStatoModel CONFIGURAZIONE = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiRichiestaApplicabilitaModel APPLICABILITA = null;
	 
	public IField NOME = null;
	 
	public IField POSIZIONE = null;
	 

	@Override
	public Class<ValidazioneContenutiApplicativiRichiesta> getModeledClass(){
		return ValidazioneContenutiApplicativiRichiesta.class;
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