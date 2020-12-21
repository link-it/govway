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

import org.openspcoop2.core.allarmi.AllarmeMail;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AllarmeMail 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeMailModel extends AbstractModel<AllarmeMail> {

	public AllarmeMailModel(){
	
		super();
	
		this.ACK_MODE = new Field("ack-mode",java.lang.Integer.class,"allarme-mail",AllarmeMail.class);
		this.INVIA_WARNING = new Field("invia-warning",java.lang.Integer.class,"allarme-mail",AllarmeMail.class);
		this.INVIA_ALERT = new Field("invia-alert",java.lang.Integer.class,"allarme-mail",AllarmeMail.class);
		this.DESTINATARI = new Field("destinatari",java.lang.String.class,"allarme-mail",AllarmeMail.class);
		this.SUBJECT = new Field("subject",java.lang.String.class,"allarme-mail",AllarmeMail.class);
		this.BODY = new Field("body",java.lang.String.class,"allarme-mail",AllarmeMail.class);
	
	}
	
	public AllarmeMailModel(IField father){
	
		super(father);
	
		this.ACK_MODE = new ComplexField(father,"ack-mode",java.lang.Integer.class,"allarme-mail",AllarmeMail.class);
		this.INVIA_WARNING = new ComplexField(father,"invia-warning",java.lang.Integer.class,"allarme-mail",AllarmeMail.class);
		this.INVIA_ALERT = new ComplexField(father,"invia-alert",java.lang.Integer.class,"allarme-mail",AllarmeMail.class);
		this.DESTINATARI = new ComplexField(father,"destinatari",java.lang.String.class,"allarme-mail",AllarmeMail.class);
		this.SUBJECT = new ComplexField(father,"subject",java.lang.String.class,"allarme-mail",AllarmeMail.class);
		this.BODY = new ComplexField(father,"body",java.lang.String.class,"allarme-mail",AllarmeMail.class);
	
	}
	
	

	public IField ACK_MODE = null;
	 
	public IField INVIA_WARNING = null;
	 
	public IField INVIA_ALERT = null;
	 
	public IField DESTINATARI = null;
	 
	public IField SUBJECT = null;
	 
	public IField BODY = null;
	 

	@Override
	public Class<AllarmeMail> getModeledClass(){
		return AllarmeMail.class;
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