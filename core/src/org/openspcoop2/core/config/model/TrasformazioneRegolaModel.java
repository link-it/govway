/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.TrasformazioneRegola;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRegola 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRegolaModel extends AbstractModel<TrasformazioneRegola> {

	public TrasformazioneRegolaModel(){
	
		super();
	
		this.APPLICABILITA = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaRichiestaModel(new Field("applicabilita",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta.class,"trasformazione-regola",TrasformazioneRegola.class));
		this.RICHIESTA = new org.openspcoop2.core.config.model.TrasformazioneRegolaRichiestaModel(new Field("richiesta",org.openspcoop2.core.config.TrasformazioneRegolaRichiesta.class,"trasformazione-regola",TrasformazioneRegola.class));
		this.RISPOSTA = new org.openspcoop2.core.config.model.TrasformazioneRegolaRispostaModel(new Field("risposta",org.openspcoop2.core.config.TrasformazioneRegolaRisposta.class,"trasformazione-regola",TrasformazioneRegola.class));
		this.NOME = new Field("nome",java.lang.String.class,"trasformazione-regola",TrasformazioneRegola.class);
		this.POSIZIONE = new Field("posizione",int.class,"trasformazione-regola",TrasformazioneRegola.class);
		this.STATO = new Field("stato",java.lang.String.class,"trasformazione-regola",TrasformazioneRegola.class);
	
	}
	
	public TrasformazioneRegolaModel(IField father){
	
		super(father);
	
		this.APPLICABILITA = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaRichiestaModel(new ComplexField(father,"applicabilita",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta.class,"trasformazione-regola",TrasformazioneRegola.class));
		this.RICHIESTA = new org.openspcoop2.core.config.model.TrasformazioneRegolaRichiestaModel(new ComplexField(father,"richiesta",org.openspcoop2.core.config.TrasformazioneRegolaRichiesta.class,"trasformazione-regola",TrasformazioneRegola.class));
		this.RISPOSTA = new org.openspcoop2.core.config.model.TrasformazioneRegolaRispostaModel(new ComplexField(father,"risposta",org.openspcoop2.core.config.TrasformazioneRegolaRisposta.class,"trasformazione-regola",TrasformazioneRegola.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"trasformazione-regola",TrasformazioneRegola.class);
		this.POSIZIONE = new ComplexField(father,"posizione",int.class,"trasformazione-regola",TrasformazioneRegola.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"trasformazione-regola",TrasformazioneRegola.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaRichiestaModel APPLICABILITA = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaRichiestaModel RICHIESTA = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaRispostaModel RISPOSTA = null;
	 
	public IField NOME = null;
	 
	public IField POSIZIONE = null;
	 
	public IField STATO = null;
	 

	@Override
	public Class<TrasformazioneRegola> getModeledClass(){
		return TrasformazioneRegola.class;
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