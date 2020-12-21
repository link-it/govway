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

import org.openspcoop2.core.config.RegistroPluginArchivio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RegistroPluginArchivio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistroPluginArchivioModel extends AbstractModel<RegistroPluginArchivio> {

	public RegistroPluginArchivioModel(){
	
		super();
	
		this.CONTENUTO = new Field("contenuto",byte[].class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.URL = new Field("url",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.DIR = new Field("dir",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.NOME = new Field("nome",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.DATA = new Field("data",java.util.Date.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.SORGENTE = new Field("sorgente",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
	
	}
	
	public RegistroPluginArchivioModel(IField father){
	
		super(father);
	
		this.CONTENUTO = new ComplexField(father,"contenuto",byte[].class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.URL = new ComplexField(father,"url",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.DIR = new ComplexField(father,"dir",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.DATA = new ComplexField(father,"data",java.util.Date.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
		this.SORGENTE = new ComplexField(father,"sorgente",java.lang.String.class,"registro-plugin-archivio",RegistroPluginArchivio.class);
	
	}
	
	

	public IField CONTENUTO = null;
	 
	public IField URL = null;
	 
	public IField DIR = null;
	 
	public IField NOME = null;
	 
	public IField DATA = null;
	 
	public IField SORGENTE = null;
	 

	@Override
	public Class<RegistroPluginArchivio> getModeledClass(){
		return RegistroPluginArchivio.class;
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