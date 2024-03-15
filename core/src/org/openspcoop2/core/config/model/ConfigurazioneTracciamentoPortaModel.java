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

import org.openspcoop2.core.config.ConfigurazioneTracciamentoPorta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneTracciamentoPorta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneTracciamentoPortaModel extends AbstractModel<ConfigurazioneTracciamentoPorta> {

	public ConfigurazioneTracciamentoPortaModel(){
	
		super();
	
		this.DATABASE = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneModel(new Field("database",org.openspcoop2.core.config.TracciamentoConfigurazione.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.FILETRACE = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneModel(new Field("filetrace",org.openspcoop2.core.config.TracciamentoConfigurazione.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.FILETRACE_CONFIG = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceModel(new Field("filetrace-config",org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.TRANSAZIONI = new org.openspcoop2.core.config.model.TransazioniModel(new Field("transazioni",org.openspcoop2.core.config.Transazioni.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.ESITI = new Field("esiti",java.lang.String.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class);
	
	}
	
	public ConfigurazioneTracciamentoPortaModel(IField father){
	
		super(father);
	
		this.DATABASE = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneModel(new ComplexField(father,"database",org.openspcoop2.core.config.TracciamentoConfigurazione.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.FILETRACE = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneModel(new ComplexField(father,"filetrace",org.openspcoop2.core.config.TracciamentoConfigurazione.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.FILETRACE_CONFIG = new org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceModel(new ComplexField(father,"filetrace-config",org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.TRANSAZIONI = new org.openspcoop2.core.config.model.TransazioniModel(new ComplexField(father,"transazioni",org.openspcoop2.core.config.Transazioni.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class));
		this.ESITI = new ComplexField(father,"esiti",java.lang.String.class,"configurazione-tracciamento-porta",ConfigurazioneTracciamentoPorta.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.TracciamentoConfigurazioneModel DATABASE = null;
	 
	public org.openspcoop2.core.config.model.TracciamentoConfigurazioneModel FILETRACE = null;
	 
	public org.openspcoop2.core.config.model.TracciamentoConfigurazioneFiletraceModel FILETRACE_CONFIG = null;
	 
	public org.openspcoop2.core.config.model.TransazioniModel TRANSAZIONI = null;
	 
	public IField ESITI = null;
	 

	@Override
	public Class<ConfigurazioneTracciamentoPorta> getModeledClass(){
		return ConfigurazioneTracciamentoPorta.class;
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