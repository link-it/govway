/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRegolaApplicabilitaServizioApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRegolaApplicabilitaServizioApplicativoModel extends AbstractModel<TrasformazioneRegolaApplicabilitaServizioApplicativo> {

	public TrasformazioneRegolaApplicabilitaServizioApplicativoModel(){
	
		super();
	
		this.TIPO_SOGGETTO_PROPRIETARIO = new Field("tipo-soggetto-proprietario",java.lang.String.class,"trasformazione-regola-applicabilita-servizio-applicativo",TrasformazioneRegolaApplicabilitaServizioApplicativo.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new Field("nome-soggetto-proprietario",java.lang.String.class,"trasformazione-regola-applicabilita-servizio-applicativo",TrasformazioneRegolaApplicabilitaServizioApplicativo.class);
		this.NOME = new Field("nome",java.lang.String.class,"trasformazione-regola-applicabilita-servizio-applicativo",TrasformazioneRegolaApplicabilitaServizioApplicativo.class);
	
	}
	
	public TrasformazioneRegolaApplicabilitaServizioApplicativoModel(IField father){
	
		super(father);
	
		this.TIPO_SOGGETTO_PROPRIETARIO = new ComplexField(father,"tipo-soggetto-proprietario",java.lang.String.class,"trasformazione-regola-applicabilita-servizio-applicativo",TrasformazioneRegolaApplicabilitaServizioApplicativo.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new ComplexField(father,"nome-soggetto-proprietario",java.lang.String.class,"trasformazione-regola-applicabilita-servizio-applicativo",TrasformazioneRegolaApplicabilitaServizioApplicativo.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"trasformazione-regola-applicabilita-servizio-applicativo",TrasformazioneRegolaApplicabilitaServizioApplicativo.class);
	
	}
	
	

	public IField TIPO_SOGGETTO_PROPRIETARIO = null;
	 
	public IField NOME_SOGGETTO_PROPRIETARIO = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<TrasformazioneRegolaApplicabilitaServizioApplicativo> getModeledClass(){
		return TrasformazioneRegolaApplicabilitaServizioApplicativo.class;
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