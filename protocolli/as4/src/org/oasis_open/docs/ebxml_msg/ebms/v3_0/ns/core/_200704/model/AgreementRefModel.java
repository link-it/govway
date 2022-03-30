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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AgreementRef 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AgreementRefModel extends AbstractModel<AgreementRef> {

	public AgreementRefModel(){
	
		super();
	
		this.BASE = new Field("base",java.lang.String.class,"AgreementRef",AgreementRef.class);
		this.TYPE = new Field("type",java.lang.String.class,"AgreementRef",AgreementRef.class);
		this.PMODE = new Field("pmode",java.lang.String.class,"AgreementRef",AgreementRef.class);
	
	}
	
	public AgreementRefModel(IField father){
	
		super(father);
	
		this.BASE = new ComplexField(father,"base",java.lang.String.class,"AgreementRef",AgreementRef.class);
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"AgreementRef",AgreementRef.class);
		this.PMODE = new ComplexField(father,"pmode",java.lang.String.class,"AgreementRef",AgreementRef.class);
	
	}
	
	

	public IField BASE = null;
	 
	public IField TYPE = null;
	 
	public IField PMODE = null;
	 

	@Override
	public Class<AgreementRef> getModeledClass(){
		return AgreementRef.class;
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