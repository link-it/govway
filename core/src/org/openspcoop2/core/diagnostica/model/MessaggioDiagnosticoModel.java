/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.core.diagnostica.model;

import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MessaggioDiagnostico 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessaggioDiagnosticoModel extends AbstractModel<MessaggioDiagnostico> {

	public MessaggioDiagnosticoModel(){
	
		super();
	
		this.DOMINIO = new org.openspcoop2.core.diagnostica.model.DominioDiagnosticoModel(new Field("dominio",org.openspcoop2.core.diagnostica.DominioDiagnostico.class,"messaggio-diagnostico",MessaggioDiagnostico.class));
		this.IDENTIFICATIVO_RICHIESTA = new Field("identificativo-richiesta",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.IDENTIFICATIVO_RISPOSTA = new Field("identificativo-risposta",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.CODICE = new Field("codice",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.MESSAGGIO = new Field("messaggio",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.SEVERITA = new Field("severita",java.lang.Integer.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.PROTOCOLLO = new org.openspcoop2.core.diagnostica.model.ProtocolloModel(new Field("protocollo",org.openspcoop2.core.diagnostica.Protocollo.class,"messaggio-diagnostico",MessaggioDiagnostico.class));
		this.FILTRO_INFORMAZIONE_PROTOCOLLO = new org.openspcoop2.core.diagnostica.model.FiltroInformazioneProtocolloModel(new Field("filtro-informazione-protocollo",org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo.class,"messaggio-diagnostico",MessaggioDiagnostico.class));
	
	}
	
	public MessaggioDiagnosticoModel(IField father){
	
		super(father);
	
		this.DOMINIO = new org.openspcoop2.core.diagnostica.model.DominioDiagnosticoModel(new ComplexField(father,"dominio",org.openspcoop2.core.diagnostica.DominioDiagnostico.class,"messaggio-diagnostico",MessaggioDiagnostico.class));
		this.IDENTIFICATIVO_RICHIESTA = new ComplexField(father,"identificativo-richiesta",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.IDENTIFICATIVO_RISPOSTA = new ComplexField(father,"identificativo-risposta",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.CODICE = new ComplexField(father,"codice",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.MESSAGGIO = new ComplexField(father,"messaggio",java.lang.String.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.SEVERITA = new ComplexField(father,"severita",java.lang.Integer.class,"messaggio-diagnostico",MessaggioDiagnostico.class);
		this.PROTOCOLLO = new org.openspcoop2.core.diagnostica.model.ProtocolloModel(new ComplexField(father,"protocollo",org.openspcoop2.core.diagnostica.Protocollo.class,"messaggio-diagnostico",MessaggioDiagnostico.class));
		this.FILTRO_INFORMAZIONE_PROTOCOLLO = new org.openspcoop2.core.diagnostica.model.FiltroInformazioneProtocolloModel(new ComplexField(father,"filtro-informazione-protocollo",org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo.class,"messaggio-diagnostico",MessaggioDiagnostico.class));
	
	}
	
	

	public org.openspcoop2.core.diagnostica.model.DominioDiagnosticoModel DOMINIO = null;
	 
	public IField IDENTIFICATIVO_RICHIESTA = null;
	 
	public IField IDENTIFICATIVO_RISPOSTA = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField CODICE = null;
	 
	public IField MESSAGGIO = null;
	 
	public IField SEVERITA = null;
	 
	public org.openspcoop2.core.diagnostica.model.ProtocolloModel PROTOCOLLO = null;
	 
	public org.openspcoop2.core.diagnostica.model.FiltroInformazioneProtocolloModel FILTRO_INFORMAZIONE_PROTOCOLLO = null;
	 

	@Override
	public Class<MessaggioDiagnostico> getModeledClass(){
		return MessaggioDiagnostico.class;
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