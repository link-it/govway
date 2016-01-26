/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Eccezione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Eccezione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioneModel extends AbstractModel<Eccezione> {

	public EccezioneModel(){
	
		super();
	
		this.CODICE = new org.openspcoop2.core.tracciamento.model.CodiceEccezioneModel(new Field("codice",org.openspcoop2.core.tracciamento.CodiceEccezione.class,"eccezione",Eccezione.class));
		this.CONTESTO_CODIFICA = new org.openspcoop2.core.tracciamento.model.ContestoCodificaEccezioneModel(new Field("contesto-codifica",org.openspcoop2.core.tracciamento.ContestoCodificaEccezione.class,"eccezione",Eccezione.class));
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"eccezione",Eccezione.class);
		this.RILEVANZA = new org.openspcoop2.core.tracciamento.model.RilevanzaEccezioneModel(new Field("rilevanza",org.openspcoop2.core.tracciamento.RilevanzaEccezione.class,"eccezione",Eccezione.class));
		this.MODULO = new Field("modulo",java.lang.String.class,"eccezione",Eccezione.class);
	
	}
	
	public EccezioneModel(IField father){
	
		super(father);
	
		this.CODICE = new org.openspcoop2.core.tracciamento.model.CodiceEccezioneModel(new ComplexField(father,"codice",org.openspcoop2.core.tracciamento.CodiceEccezione.class,"eccezione",Eccezione.class));
		this.CONTESTO_CODIFICA = new org.openspcoop2.core.tracciamento.model.ContestoCodificaEccezioneModel(new ComplexField(father,"contesto-codifica",org.openspcoop2.core.tracciamento.ContestoCodificaEccezione.class,"eccezione",Eccezione.class));
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"eccezione",Eccezione.class);
		this.RILEVANZA = new org.openspcoop2.core.tracciamento.model.RilevanzaEccezioneModel(new ComplexField(father,"rilevanza",org.openspcoop2.core.tracciamento.RilevanzaEccezione.class,"eccezione",Eccezione.class));
		this.MODULO = new ComplexField(father,"modulo",java.lang.String.class,"eccezione",Eccezione.class);
	
	}
	
	

	public org.openspcoop2.core.tracciamento.model.CodiceEccezioneModel CODICE = null;
	 
	public org.openspcoop2.core.tracciamento.model.ContestoCodificaEccezioneModel CONTESTO_CODIFICA = null;
	 
	public IField DESCRIZIONE = null;
	 
	public org.openspcoop2.core.tracciamento.model.RilevanzaEccezioneModel RILEVANZA = null;
	 
	public IField MODULO = null;
	 

	@Override
	public Class<Eccezione> getModeledClass(){
		return Eccezione.class;
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