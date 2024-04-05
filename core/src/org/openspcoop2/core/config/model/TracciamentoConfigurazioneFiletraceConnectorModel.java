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

import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TracciamentoConfigurazioneFiletraceConnector 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciamentoConfigurazioneFiletraceConnectorModel extends AbstractModel<TracciamentoConfigurazioneFiletraceConnector> {

	public TracciamentoConfigurazioneFiletraceConnectorModel(){
	
		super();
	
		this.STATO = new Field("stato",java.lang.String.class,"tracciamento-configurazione-filetrace-connector",TracciamentoConfigurazioneFiletraceConnector.class);
		this.PAYLOAD = new Field("payload",java.lang.String.class,"tracciamento-configurazione-filetrace-connector",TracciamentoConfigurazioneFiletraceConnector.class);
		this.HEADER = new Field("header",java.lang.String.class,"tracciamento-configurazione-filetrace-connector",TracciamentoConfigurazioneFiletraceConnector.class);
	
	}
	
	public TracciamentoConfigurazioneFiletraceConnectorModel(IField father){
	
		super(father);
	
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"tracciamento-configurazione-filetrace-connector",TracciamentoConfigurazioneFiletraceConnector.class);
		this.PAYLOAD = new ComplexField(father,"payload",java.lang.String.class,"tracciamento-configurazione-filetrace-connector",TracciamentoConfigurazioneFiletraceConnector.class);
		this.HEADER = new ComplexField(father,"header",java.lang.String.class,"tracciamento-configurazione-filetrace-connector",TracciamentoConfigurazioneFiletraceConnector.class);
	
	}
	
	

	public IField STATO = null;
	 
	public IField PAYLOAD = null;
	 
	public IField HEADER = null;
	 

	@Override
	public Class<TracciamentoConfigurazioneFiletraceConnector> getModeledClass(){
		return TracciamentoConfigurazioneFiletraceConnector.class;
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