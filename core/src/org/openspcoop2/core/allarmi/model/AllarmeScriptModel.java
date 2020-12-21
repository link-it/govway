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
package org.openspcoop2.core.allarmi.model;

import org.openspcoop2.core.allarmi.AllarmeScript;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AllarmeScript 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeScriptModel extends AbstractModel<AllarmeScript> {

	public AllarmeScriptModel(){
	
		super();
	
		this.ACK_MODE = new Field("ack-mode",java.lang.Integer.class,"allarme-script",AllarmeScript.class);
		this.INVOCA_WARNING = new Field("invoca-warning",java.lang.Integer.class,"allarme-script",AllarmeScript.class);
		this.INVOCA_ALERT = new Field("invoca-alert",java.lang.Integer.class,"allarme-script",AllarmeScript.class);
		this.COMMAND = new Field("command",java.lang.String.class,"allarme-script",AllarmeScript.class);
		this.ARGS = new Field("args",java.lang.String.class,"allarme-script",AllarmeScript.class);
	
	}
	
	public AllarmeScriptModel(IField father){
	
		super(father);
	
		this.ACK_MODE = new ComplexField(father,"ack-mode",java.lang.Integer.class,"allarme-script",AllarmeScript.class);
		this.INVOCA_WARNING = new ComplexField(father,"invoca-warning",java.lang.Integer.class,"allarme-script",AllarmeScript.class);
		this.INVOCA_ALERT = new ComplexField(father,"invoca-alert",java.lang.Integer.class,"allarme-script",AllarmeScript.class);
		this.COMMAND = new ComplexField(father,"command",java.lang.String.class,"allarme-script",AllarmeScript.class);
		this.ARGS = new ComplexField(father,"args",java.lang.String.class,"allarme-script",AllarmeScript.class);
	
	}
	
	

	public IField ACK_MODE = null;
	 
	public IField INVOCA_WARNING = null;
	 
	public IField INVOCA_ALERT = null;
	 
	public IField COMMAND = null;
	 
	public IField ARGS = null;
	 

	@Override
	public Class<AllarmeScript> getModeledClass(){
		return AllarmeScript.class;
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