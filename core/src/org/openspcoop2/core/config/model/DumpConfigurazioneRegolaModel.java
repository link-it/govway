/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.DumpConfigurazioneRegola;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DumpConfigurazioneRegola 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpConfigurazioneRegolaModel extends AbstractModel<DumpConfigurazioneRegola> {

	public DumpConfigurazioneRegolaModel(){
	
		super();
	
		this.PAYLOAD = new Field("payload",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.PAYLOAD_PARSING = new Field("payload-parsing",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.BODY = new Field("body",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.ATTACHMENTS = new Field("attachments",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.HEADERS = new Field("headers",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
	
	}
	
	public DumpConfigurazioneRegolaModel(IField father){
	
		super(father);
	
		this.PAYLOAD = new ComplexField(father,"payload",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.PAYLOAD_PARSING = new ComplexField(father,"payload-parsing",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.BODY = new ComplexField(father,"body",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.ATTACHMENTS = new ComplexField(father,"attachments",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
		this.HEADERS = new ComplexField(father,"headers",java.lang.String.class,"dump-configurazione-regola",DumpConfigurazioneRegola.class);
	
	}
	
	

	public IField PAYLOAD = null;
	 
	public IField PAYLOAD_PARSING = null;
	 
	public IField BODY = null;
	 
	public IField ATTACHMENTS = null;
	 
	public IField HEADERS = null;
	 

	@Override
	public Class<DumpConfigurazioneRegola> getModeledClass(){
		return DumpConfigurazioneRegola.class;
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