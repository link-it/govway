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
package it.cnipa.schemas._2003.egovit.exception1_0.model;

import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model EccezioneProcessamento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioneProcessamentoModel extends AbstractModel<EccezioneProcessamento> {

	public EccezioneProcessamentoModel(){
	
		super();
	
		this.CODICE_ECCEZIONE = new Field("codiceEccezione",java.lang.String.class,"EccezioneProcessamento",EccezioneProcessamento.class);
		this.DESCRIZIONE_ECCEZIONE = new Field("descrizioneEccezione",java.lang.String.class,"EccezioneProcessamento",EccezioneProcessamento.class);
	
	}
	
	public EccezioneProcessamentoModel(IField father){
	
		super(father);
	
		this.CODICE_ECCEZIONE = new ComplexField(father,"codiceEccezione",java.lang.String.class,"EccezioneProcessamento",EccezioneProcessamento.class);
		this.DESCRIZIONE_ECCEZIONE = new ComplexField(father,"descrizioneEccezione",java.lang.String.class,"EccezioneProcessamento",EccezioneProcessamento.class);
	
	}
	
	

	public IField CODICE_ECCEZIONE = null;
	 
	public IField DESCRIZIONE_ECCEZIONE = null;
	 

	@Override
	public Class<EccezioneProcessamento> getModeledClass(){
		return EccezioneProcessamento.class;
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