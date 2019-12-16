/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import eu.domibus.configuration.Mpc;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Mpc 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MpcModel extends AbstractModel<Mpc> {

	public MpcModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"mpc",Mpc.class);
		this.RETENTION_DOWNLOADED = new Field("retention_downloaded",java.math.BigInteger.class,"mpc",Mpc.class);
		this.RETENTION_UNDOWNLOADED = new Field("retention_undownloaded",java.math.BigInteger.class,"mpc",Mpc.class);
		this.DEFAULT = new Field("default",boolean.class,"mpc",Mpc.class);
		this.ENABLED = new Field("enabled",boolean.class,"mpc",Mpc.class);
		this.QUALIFIED_NAME = new Field("qualifiedName",java.lang.String.class,"mpc",Mpc.class);
	
	}
	
	public MpcModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"mpc",Mpc.class);
		this.RETENTION_DOWNLOADED = new ComplexField(father,"retention_downloaded",java.math.BigInteger.class,"mpc",Mpc.class);
		this.RETENTION_UNDOWNLOADED = new ComplexField(father,"retention_undownloaded",java.math.BigInteger.class,"mpc",Mpc.class);
		this.DEFAULT = new ComplexField(father,"default",boolean.class,"mpc",Mpc.class);
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"mpc",Mpc.class);
		this.QUALIFIED_NAME = new ComplexField(father,"qualifiedName",java.lang.String.class,"mpc",Mpc.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField RETENTION_DOWNLOADED = null;
	 
	public IField RETENTION_UNDOWNLOADED = null;
	 
	public IField DEFAULT = null;
	 
	public IField ENABLED = null;
	 
	public IField QUALIFIED_NAME = null;
	 

	@Override
	public Class<Mpc> getModeledClass(){
		return Mpc.class;
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