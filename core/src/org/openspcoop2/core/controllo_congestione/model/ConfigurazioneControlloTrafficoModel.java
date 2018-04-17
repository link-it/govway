/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.controllo_congestione.model;

import org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneControlloTraffico 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTrafficoModel extends AbstractModel<ConfigurazioneControlloTraffico> {

	public ConfigurazioneControlloTrafficoModel(){
	
		super();
	
		this.CONTROLLO_MAX_THREADS_ENABLED = new Field("controllo-max-threads-enabled",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_WARNING_ONLY = new Field("controllo-max-threads-warning-only",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_SOGLIA = new Field("controllo-max-threads-soglia",java.lang.Long.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_TIPO_ERRORE = new Field("controllo-max-threads-tipo-errore",java.lang.String.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE = new Field("controllo-max-threads-tipo-errore-includi-descrizione",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_CONGESTIONE_ENABLED = new Field("controllo-congestione-enabled",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_CONGESTIONE_THRESHOLD = new Field("controllo-congestione-threshold",java.lang.Integer.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
	
	}
	
	public ConfigurazioneControlloTrafficoModel(IField father){
	
		super(father);
	
		this.CONTROLLO_MAX_THREADS_ENABLED = new ComplexField(father,"controllo-max-threads-enabled",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_WARNING_ONLY = new ComplexField(father,"controllo-max-threads-warning-only",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_SOGLIA = new ComplexField(father,"controllo-max-threads-soglia",java.lang.Long.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_TIPO_ERRORE = new ComplexField(father,"controllo-max-threads-tipo-errore",java.lang.String.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE = new ComplexField(father,"controllo-max-threads-tipo-errore-includi-descrizione",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_CONGESTIONE_ENABLED = new ComplexField(father,"controllo-congestione-enabled",boolean.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
		this.CONTROLLO_CONGESTIONE_THRESHOLD = new ComplexField(father,"controllo-congestione-threshold",java.lang.Integer.class,"configurazione-controllo-traffico",ConfigurazioneControlloTraffico.class);
	
	}
	
	

	public IField CONTROLLO_MAX_THREADS_ENABLED = null;
	 
	public IField CONTROLLO_MAX_THREADS_WARNING_ONLY = null;
	 
	public IField CONTROLLO_MAX_THREADS_SOGLIA = null;
	 
	public IField CONTROLLO_MAX_THREADS_TIPO_ERRORE = null;
	 
	public IField CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE = null;
	 
	public IField CONTROLLO_CONGESTIONE_ENABLED = null;
	 
	public IField CONTROLLO_CONGESTIONE_THRESHOLD = null;
	 

	@Override
	public Class<ConfigurazioneControlloTraffico> getModeledClass(){
		return ConfigurazioneControlloTraffico.class;
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