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
package eu.domibus.configuration.model;

import eu.domibus.configuration.LegConfiguration;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model LegConfiguration 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LegConfigurationModel extends AbstractModel<LegConfiguration> {

	public LegConfigurationModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.SERVICE = new Field("service",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.ACTION = new Field("action",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.SECURITY = new Field("security",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.DEFAULT_MPC = new Field("defaultMpc",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.RECEPTION_AWARENESS = new Field("receptionAwareness",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.RELIABILITY = new Field("reliability",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.PROPERTY_SET = new Field("propertySet",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.PAYLOAD_PROFILE = new Field("payloadProfile",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.ERROR_HANDLING = new Field("errorHandling",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.COMPRESS_PAYLOADS = new Field("compressPayloads",java.lang.String.class,"legConfiguration",LegConfiguration.class);
	
	}
	
	public LegConfigurationModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.SERVICE = new ComplexField(father,"service",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.ACTION = new ComplexField(father,"action",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.SECURITY = new ComplexField(father,"security",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.DEFAULT_MPC = new ComplexField(father,"defaultMpc",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.RECEPTION_AWARENESS = new ComplexField(father,"receptionAwareness",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.RELIABILITY = new ComplexField(father,"reliability",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.PROPERTY_SET = new ComplexField(father,"propertySet",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.PAYLOAD_PROFILE = new ComplexField(father,"payloadProfile",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.ERROR_HANDLING = new ComplexField(father,"errorHandling",java.lang.String.class,"legConfiguration",LegConfiguration.class);
		this.COMPRESS_PAYLOADS = new ComplexField(father,"compressPayloads",java.lang.String.class,"legConfiguration",LegConfiguration.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField SERVICE = null;
	 
	public IField ACTION = null;
	 
	public IField SECURITY = null;
	 
	public IField DEFAULT_MPC = null;
	 
	public IField RECEPTION_AWARENESS = null;
	 
	public IField RELIABILITY = null;
	 
	public IField PROPERTY_SET = null;
	 
	public IField PAYLOAD_PROFILE = null;
	 
	public IField ERROR_HANDLING = null;
	 
	public IField COMPRESS_PAYLOADS = null;
	 

	@Override
	public Class<LegConfiguration> getModeledClass(){
		return LegConfiguration.class;
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