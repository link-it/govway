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

import org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ValidazioneContenutiApplicativiStato 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneContenutiApplicativiStatoModel extends AbstractModel<ValidazioneContenutiApplicativiStato> {

	public ValidazioneContenutiApplicativiStatoModel(){
	
		super();
	
		this.CONFIGURAZIONE_PATTERN = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiPatternModel(new Field("configurazione-pattern",org.openspcoop2.core.config.ValidazioneContenutiApplicativiPattern.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class));
		this.STATO = new Field("stato",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.ACCEPT_MTOM_MESSAGE = new Field("accept-mtom-message",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.SOAP_ACTION = new Field("soap-action",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.JSON_SCHEMA = new Field("json-schema",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
	
	}
	
	public ValidazioneContenutiApplicativiStatoModel(IField father){
	
		super(father);
	
		this.CONFIGURAZIONE_PATTERN = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiPatternModel(new ComplexField(father,"configurazione-pattern",org.openspcoop2.core.config.ValidazioneContenutiApplicativiPattern.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class));
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.ACCEPT_MTOM_MESSAGE = new ComplexField(father,"accept-mtom-message",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.SOAP_ACTION = new ComplexField(father,"soap-action",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
		this.JSON_SCHEMA = new ComplexField(father,"json-schema",java.lang.String.class,"validazione-contenuti-applicativi-stato",ValidazioneContenutiApplicativiStato.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiPatternModel CONFIGURAZIONE_PATTERN = null;
	 
	public IField STATO = null;
	 
	public IField TIPO = null;
	 
	public IField ACCEPT_MTOM_MESSAGE = null;
	 
	public IField SOAP_ACTION = null;
	 
	public IField JSON_SCHEMA = null;
	 

	@Override
	public Class<ValidazioneContenutiApplicativiStato> getModeledClass(){
		return ValidazioneContenutiApplicativiStato.class;
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