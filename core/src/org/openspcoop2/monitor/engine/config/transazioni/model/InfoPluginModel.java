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
package org.openspcoop2.monitor.engine.config.transazioni.model;

import org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model InfoPlugin 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoPluginModel extends AbstractModel<InfoPlugin> {

	public InfoPluginModel(){
	
		super();
	
		this.TIPO_PLUGIN = new Field("tipo-plugin",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.CLASS_NAME = new Field("class-name",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.LABEL = new Field("label",java.lang.String.class,"info-plugin",InfoPlugin.class);
	
	}
	
	public InfoPluginModel(IField father){
	
		super(father);
	
		this.TIPO_PLUGIN = new ComplexField(father,"tipo-plugin",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.CLASS_NAME = new ComplexField(father,"class-name",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"info-plugin",InfoPlugin.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"info-plugin",InfoPlugin.class);
	
	}
	
	

	public IField TIPO_PLUGIN = null;
	 
	public IField TIPO = null;
	 
	public IField CLASS_NAME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField LABEL = null;
	 

	@Override
	public Class<InfoPlugin> getModeledClass(){
		return InfoPlugin.class;
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