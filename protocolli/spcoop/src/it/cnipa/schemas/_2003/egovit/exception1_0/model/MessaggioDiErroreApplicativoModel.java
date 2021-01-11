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
package it.cnipa.schemas._2003.egovit.exception1_0.model;

import it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MessaggioDiErroreApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessaggioDiErroreApplicativoModel extends AbstractModel<MessaggioDiErroreApplicativo> {

	public MessaggioDiErroreApplicativoModel(){
	
		super();
	
		this.ORA_REGISTRAZIONE = new Field("OraRegistrazione",java.util.Date.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class);
		this.IDENTIFICATIVO_PORTA = new Field("IdentificativoPorta",java.lang.String.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class);
		this.IDENTIFICATIVO_FUNZIONE = new Field("IdentificativoFunzione",java.lang.String.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class);
		this.ECCEZIONE = new it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneModel(new Field("Eccezione",it.cnipa.schemas._2003.egovit.exception1_0.Eccezione.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class));
	
	}
	
	public MessaggioDiErroreApplicativoModel(IField father){
	
		super(father);
	
		this.ORA_REGISTRAZIONE = new ComplexField(father,"OraRegistrazione",java.util.Date.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class);
		this.IDENTIFICATIVO_PORTA = new ComplexField(father,"IdentificativoPorta",java.lang.String.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class);
		this.IDENTIFICATIVO_FUNZIONE = new ComplexField(father,"IdentificativoFunzione",java.lang.String.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class);
		this.ECCEZIONE = new it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneModel(new ComplexField(father,"Eccezione",it.cnipa.schemas._2003.egovit.exception1_0.Eccezione.class,"MessaggioDiErroreApplicativo",MessaggioDiErroreApplicativo.class));
	
	}
	
	

	public IField ORA_REGISTRAZIONE = null;
	 
	public IField IDENTIFICATIVO_PORTA = null;
	 
	public IField IDENTIFICATIVO_FUNZIONE = null;
	 
	public it.cnipa.schemas._2003.egovit.exception1_0.model.EccezioneModel ECCEZIONE = null;
	 

	@Override
	public Class<MessaggioDiErroreApplicativo> getModeledClass(){
		return MessaggioDiErroreApplicativo.class;
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