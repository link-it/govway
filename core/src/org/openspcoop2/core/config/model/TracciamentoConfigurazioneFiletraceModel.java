/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TracciamentoConfigurazioneFiletrace 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciamentoConfigurazioneFiletraceModel extends AbstractModel<TracciamentoConfigurazioneFiletrace> {

	public TracciamentoConfigurazioneFiletraceModel(){
	
		super();
	
		this.DUMP_IN = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceConnectorModel(new Field("dump-in",org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector.class,"tracciamento-configurazione-filetrace",TracciamentoConfigurazioneFiletrace.class));
		this.DUMP_OUT = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceConnectorModel(new Field("dump-out",org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector.class,"tracciamento-configurazione-filetrace",TracciamentoConfigurazioneFiletrace.class));
		this.CONFIG = new Field("config",java.lang.String.class,"tracciamento-configurazione-filetrace",TracciamentoConfigurazioneFiletrace.class);
	
	}
	
	public TracciamentoConfigurazioneFiletraceModel(IField father){
	
		super(father);
	
		this.DUMP_IN = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceConnectorModel(new ComplexField(father,"dump-in",org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector.class,"tracciamento-configurazione-filetrace",TracciamentoConfigurazioneFiletrace.class));
		this.DUMP_OUT = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceConnectorModel(new ComplexField(father,"dump-out",org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector.class,"tracciamento-configurazione-filetrace",TracciamentoConfigurazioneFiletrace.class));
		this.CONFIG = new ComplexField(father,"config",java.lang.String.class,"tracciamento-configurazione-filetrace",TracciamentoConfigurazioneFiletrace.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceConnectorModel DUMP_IN = null;
	 
	public org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceConnectorModel DUMP_OUT = null;
	 
	public IField CONFIG = null;
	 

	@Override
	public Class<TracciamentoConfigurazioneFiletrace> getModeledClass(){
		return TracciamentoConfigurazioneFiletrace.class;
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