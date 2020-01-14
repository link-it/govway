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
package org.openspcoop2.core.controllo_traffico.model;

import org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TempiRispostaFruizione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TempiRispostaFruizioneModel extends AbstractModel<TempiRispostaFruizione> {

	public TempiRispostaFruizioneModel(){
	
		super();
	
		this.CONNECTION_TIMEOUT = new Field("connection-timeout",java.lang.Integer.class,"tempi-risposta-fruizione",TempiRispostaFruizione.class);
		this.READ_TIMEOUT = new Field("read-timeout",java.lang.Integer.class,"tempi-risposta-fruizione",TempiRispostaFruizione.class);
		this.TEMPO_MEDIO_RISPOSTA = new Field("tempo-medio-risposta",java.lang.Integer.class,"tempi-risposta-fruizione",TempiRispostaFruizione.class);
	
	}
	
	public TempiRispostaFruizioneModel(IField father){
	
		super(father);
	
		this.CONNECTION_TIMEOUT = new ComplexField(father,"connection-timeout",java.lang.Integer.class,"tempi-risposta-fruizione",TempiRispostaFruizione.class);
		this.READ_TIMEOUT = new ComplexField(father,"read-timeout",java.lang.Integer.class,"tempi-risposta-fruizione",TempiRispostaFruizione.class);
		this.TEMPO_MEDIO_RISPOSTA = new ComplexField(father,"tempo-medio-risposta",java.lang.Integer.class,"tempi-risposta-fruizione",TempiRispostaFruizione.class);
	
	}
	
	

	public IField CONNECTION_TIMEOUT = null;
	 
	public IField READ_TIMEOUT = null;
	 
	public IField TEMPO_MEDIO_RISPOSTA = null;
	 

	@Override
	public Class<TempiRispostaFruizione> getModeledClass(){
		return TempiRispostaFruizione.class;
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