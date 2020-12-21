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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.RegistroPlugin;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RegistroPlugin 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistroPluginModel extends AbstractModel<RegistroPlugin> {

	public RegistroPluginModel(){
	
		super();
	
		this.ARCHIVIO = new org.openspcoop2.core.config.model.RegistroPluginArchivioModel(new Field("archivio",org.openspcoop2.core.config.RegistroPluginArchivio.class,"registro-plugin",RegistroPlugin.class));
		this.COMPATIBILITA = new Field("compatibilita",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.STATO = new Field("stato",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.NOME = new Field("nome",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.POSIZIONE = new Field("posizione",int.class,"registro-plugin",RegistroPlugin.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.DATA = new Field("data",java.util.Date.class,"registro-plugin",RegistroPlugin.class);
	
	}
	
	public RegistroPluginModel(IField father){
	
		super(father);
	
		this.ARCHIVIO = new org.openspcoop2.core.config.model.RegistroPluginArchivioModel(new ComplexField(father,"archivio",org.openspcoop2.core.config.RegistroPluginArchivio.class,"registro-plugin",RegistroPlugin.class));
		this.COMPATIBILITA = new ComplexField(father,"compatibilita",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.POSIZIONE = new ComplexField(father,"posizione",int.class,"registro-plugin",RegistroPlugin.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"registro-plugin",RegistroPlugin.class);
		this.DATA = new ComplexField(father,"data",java.util.Date.class,"registro-plugin",RegistroPlugin.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.RegistroPluginArchivioModel ARCHIVIO = null;
	 
	public IField COMPATIBILITA = null;
	 
	public IField STATO = null;
	 
	public IField NOME = null;
	 
	public IField POSIZIONE = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField DATA = null;
	 

	@Override
	public Class<RegistroPlugin> getModeledClass(){
		return RegistroPlugin.class;
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