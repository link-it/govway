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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.MessaggiDiagnostici;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MessaggiDiagnostici 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessaggiDiagnosticiModel extends AbstractModel<MessaggiDiagnostici> {

	public MessaggiDiagnosticiModel(){
	
		super();
	
		this.OPENSPCOOP_APPENDER = new org.openspcoop2.core.config.model.OpenspcoopAppenderModel(new Field("openspcoop-appender",org.openspcoop2.core.config.OpenspcoopAppender.class,"messaggi-diagnostici",MessaggiDiagnostici.class));
		this.OPENSPCOOP_SORGENTE_DATI = new org.openspcoop2.core.config.model.OpenspcoopSorgenteDatiModel(new Field("openspcoop-sorgente-dati",org.openspcoop2.core.config.OpenspcoopSorgenteDati.class,"messaggi-diagnostici",MessaggiDiagnostici.class));
		this.SEVERITA = new Field("severita",java.lang.String.class,"messaggi-diagnostici",MessaggiDiagnostici.class);
		this.SEVERITA_LOG_4J = new Field("severita-log4j",java.lang.String.class,"messaggi-diagnostici",MessaggiDiagnostici.class);
	
	}
	
	public MessaggiDiagnosticiModel(IField father){
	
		super(father);
	
		this.OPENSPCOOP_APPENDER = new org.openspcoop2.core.config.model.OpenspcoopAppenderModel(new ComplexField(father,"openspcoop-appender",org.openspcoop2.core.config.OpenspcoopAppender.class,"messaggi-diagnostici",MessaggiDiagnostici.class));
		this.OPENSPCOOP_SORGENTE_DATI = new org.openspcoop2.core.config.model.OpenspcoopSorgenteDatiModel(new ComplexField(father,"openspcoop-sorgente-dati",org.openspcoop2.core.config.OpenspcoopSorgenteDati.class,"messaggi-diagnostici",MessaggiDiagnostici.class));
		this.SEVERITA = new ComplexField(father,"severita",java.lang.String.class,"messaggi-diagnostici",MessaggiDiagnostici.class);
		this.SEVERITA_LOG_4J = new ComplexField(father,"severita-log4j",java.lang.String.class,"messaggi-diagnostici",MessaggiDiagnostici.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.OpenspcoopAppenderModel OPENSPCOOP_APPENDER = null;
	 
	public org.openspcoop2.core.config.model.OpenspcoopSorgenteDatiModel OPENSPCOOP_SORGENTE_DATI = null;
	 
	public IField SEVERITA = null;
	 
	public IField SEVERITA_LOG_4J = null;
	 

	@Override
	public Class<MessaggiDiagnostici> getModeledClass(){
		return MessaggiDiagnostici.class;
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