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

import org.openspcoop2.core.config.Dump;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Dump 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpModel extends AbstractModel<Dump> {

	public DumpModel(){
	
		super();
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new Field("configurazione",org.openspcoop2.core.config.DumpConfigurazione.class,"dump",Dump.class));
		this.CONFIGURAZIONE_PORTA_DELEGATA = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new Field("configurazione-porta-delegata",org.openspcoop2.core.config.DumpConfigurazione.class,"dump",Dump.class));
		this.CONFIGURAZIONE_PORTA_APPLICATIVA = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new Field("configurazione-porta-applicativa",org.openspcoop2.core.config.DumpConfigurazione.class,"dump",Dump.class));
		this.OPENSPCOOP_APPENDER = new org.openspcoop2.core.config.model.OpenspcoopAppenderModel(new Field("openspcoop-appender",org.openspcoop2.core.config.OpenspcoopAppender.class,"dump",Dump.class));
		this.STATO = new Field("stato",java.lang.String.class,"dump",Dump.class);
		this.DUMP_BINARIO_PORTA_DELEGATA = new Field("dump-binario-porta-delegata",java.lang.String.class,"dump",Dump.class);
		this.DUMP_BINARIO_PORTA_APPLICATIVA = new Field("dump-binario-porta-applicativa",java.lang.String.class,"dump",Dump.class);
	
	}
	
	public DumpModel(IField father){
	
		super(father);
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new ComplexField(father,"configurazione",org.openspcoop2.core.config.DumpConfigurazione.class,"dump",Dump.class));
		this.CONFIGURAZIONE_PORTA_DELEGATA = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new ComplexField(father,"configurazione-porta-delegata",org.openspcoop2.core.config.DumpConfigurazione.class,"dump",Dump.class));
		this.CONFIGURAZIONE_PORTA_APPLICATIVA = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new ComplexField(father,"configurazione-porta-applicativa",org.openspcoop2.core.config.DumpConfigurazione.class,"dump",Dump.class));
		this.OPENSPCOOP_APPENDER = new org.openspcoop2.core.config.model.OpenspcoopAppenderModel(new ComplexField(father,"openspcoop-appender",org.openspcoop2.core.config.OpenspcoopAppender.class,"dump",Dump.class));
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"dump",Dump.class);
		this.DUMP_BINARIO_PORTA_DELEGATA = new ComplexField(father,"dump-binario-porta-delegata",java.lang.String.class,"dump",Dump.class);
		this.DUMP_BINARIO_PORTA_APPLICATIVA = new ComplexField(father,"dump-binario-porta-applicativa",java.lang.String.class,"dump",Dump.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.DumpConfigurazioneModel CONFIGURAZIONE = null;
	 
	public org.openspcoop2.core.config.model.DumpConfigurazioneModel CONFIGURAZIONE_PORTA_DELEGATA = null;
	 
	public org.openspcoop2.core.config.model.DumpConfigurazioneModel CONFIGURAZIONE_PORTA_APPLICATIVA = null;
	 
	public org.openspcoop2.core.config.model.OpenspcoopAppenderModel OPENSPCOOP_APPENDER = null;
	 
	public IField STATO = null;
	 
	public IField DUMP_BINARIO_PORTA_DELEGATA = null;
	 
	public IField DUMP_BINARIO_PORTA_APPLICATIVA = null;
	 

	@Override
	public Class<Dump> getModeledClass(){
		return Dump.class;
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