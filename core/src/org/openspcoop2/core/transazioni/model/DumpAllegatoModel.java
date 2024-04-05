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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.DumpAllegato;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DumpAllegato 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpAllegatoModel extends AbstractModel<DumpAllegato> {

	public DumpAllegatoModel(){
	
		super();
	
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"dump-allegato",DumpAllegato.class);
		this.CONTENT_ID = new Field("content-id",java.lang.String.class,"dump-allegato",DumpAllegato.class);
		this.CONTENT_LOCATION = new Field("content-location",java.lang.String.class,"dump-allegato",DumpAllegato.class);
		this.ALLEGATO = new Field("allegato",byte[].class,"dump-allegato",DumpAllegato.class);
		this.HEADER = new org.openspcoop2.core.transazioni.model.DumpHeaderAllegatoModel(new Field("header",org.openspcoop2.core.transazioni.DumpHeaderAllegato.class,"dump-allegato",DumpAllegato.class));
		this.DUMP_TIMESTAMP = new Field("dump-timestamp",java.util.Date.class,"dump-allegato",DumpAllegato.class);
		this.HEADER_EXT = new Field("header-ext",java.lang.String.class,"dump-allegato",DumpAllegato.class);
	
	}
	
	public DumpAllegatoModel(IField father){
	
		super(father);
	
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"dump-allegato",DumpAllegato.class);
		this.CONTENT_ID = new ComplexField(father,"content-id",java.lang.String.class,"dump-allegato",DumpAllegato.class);
		this.CONTENT_LOCATION = new ComplexField(father,"content-location",java.lang.String.class,"dump-allegato",DumpAllegato.class);
		this.ALLEGATO = new ComplexField(father,"allegato",byte[].class,"dump-allegato",DumpAllegato.class);
		this.HEADER = new org.openspcoop2.core.transazioni.model.DumpHeaderAllegatoModel(new ComplexField(father,"header",org.openspcoop2.core.transazioni.DumpHeaderAllegato.class,"dump-allegato",DumpAllegato.class));
		this.DUMP_TIMESTAMP = new ComplexField(father,"dump-timestamp",java.util.Date.class,"dump-allegato",DumpAllegato.class);
		this.HEADER_EXT = new ComplexField(father,"header-ext",java.lang.String.class,"dump-allegato",DumpAllegato.class);
	
	}
	
	

	public IField CONTENT_TYPE = null;
	 
	public IField CONTENT_ID = null;
	 
	public IField CONTENT_LOCATION = null;
	 
	public IField ALLEGATO = null;
	 
	public org.openspcoop2.core.transazioni.model.DumpHeaderAllegatoModel HEADER = null;
	 
	public IField DUMP_TIMESTAMP = null;
	 
	public IField HEADER_EXT = null;
	 

	@Override
	public Class<DumpAllegato> getModeledClass(){
		return DumpAllegato.class;
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