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

package org.openspcoop2.pdd.core.trasformazioni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.constants.ServiceBinding;

/**
 * Tipi di Trasformazione
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoTrasformazione implements IEnumeration , Serializable , Cloneable {

	EMPTY ("empty-payload", "HTTP Payload vuoto", null),
	TEMPLATE ("template", "Template", null),
	FREEMARKER_TEMPLATE ("freemarker", "Freemarker Template", null),
	FREEMARKER_TEMPLATE_ZIP ("freemarker-zip", "Freemarker Template (Archivio Zip)", null),
	VELOCITY_TEMPLATE ("velocity", "Velocity Template", null),
	VELOCITY_TEMPLATE_ZIP ("velocity-zip", "Velocity Template (Archivio Zip)", null),
	XSLT ("xslt", "XSLT" , null),
	ZIP ("zip", "ZIP Compressor" , null),
	TGZ ("tgz", "TGZ Compressor" , null),
	TAR ("tar", "TAR Compressor" , null);
//	XML2JSON ("xml2json", ServiceBinding.REST),
//	JSON2XML ("xml2json", ServiceBinding.REST);
	
	
	/** Value */
	private String value;
	private String label;
	private ServiceBinding serviceBinding;
	@Override
	public String getValue()
	{
		return this.value;
	}
	public String getLabel() {
		return this.label;
	}
	public String getLabel(ServiceBinding serviceBinding) {
		if(this.equals(TipoTrasformazione.EMPTY) && ServiceBinding.SOAP.equals(serviceBinding)) {
			return "SOAP Body vuoto";
		}
		else {
			return this.label;
		}
	}


	/** Official Constructor */
	TipoTrasformazione(String value, String label, ServiceBinding serviceBinding)
	{
		this.value = value;
		this.label = label;
		this.serviceBinding = serviceBinding;
	}

	public String getExt() {
		switch (this) {
		case EMPTY:
			return null;
		case TEMPLATE:
			return ".gwt";
		case FREEMARKER_TEMPLATE:
			return ".ftl";
		case FREEMARKER_TEMPLATE_ZIP:
			return ".ftl.zip";
		case VELOCITY_TEMPLATE:
			return ".vm"; //".vtl";
		case VELOCITY_TEMPLATE_ZIP:
			return ".vm.zip"; //".vtl.zip";
		case XSLT:
			return ".xslt";
		case ZIP:
			return ".zip.gw";
		case TGZ:
			return ".tgz.gw";
		case TAR:
			return ".tar.gw";
		}
		
		return null;
	}

	public boolean isTemplateRequired() {
		return TipoTrasformazione.TEMPLATE.equals(this) 
				|| 
				TipoTrasformazione.FREEMARKER_TEMPLATE.equals(this)
				|| 
				TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP.equals(this)
				|| 
				TipoTrasformazione.VELOCITY_TEMPLATE.equals(this)
				|| 
				TipoTrasformazione.VELOCITY_TEMPLATE_ZIP.equals(this)
				|| 
				TipoTrasformazione.XSLT.equals(this)
				|| 
				TipoTrasformazione.ZIP.equals(this)
				|| 
				TipoTrasformazione.TGZ.equals(this)
				|| 
				TipoTrasformazione.TAR.equals(this);
	}
	
	public boolean isContentTypeEnabled() {
		return !TipoTrasformazione.EMPTY.equals(this);
	}
	
	public boolean isTrasformazioneProtocolloEnabled() {
		//return !TipoTrasformazione.EMPTY.equals(this);
		return true; // sempre
	}
	
	public boolean isBinaryMessage() {
		if(TipoTrasformazione.ZIP.equals(this) ||
				TipoTrasformazione.TGZ.equals(this) ||
				TipoTrasformazione.TAR.equals(this)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoTrasformazione object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof TipoTrasformazione) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoTrasformazione)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static String[] toArray(){
		return toArray(null);
	}
	public static String[] toArray(ServiceBinding serviceBinding){
		List<String> l = toList(serviceBinding);
		if(l!=null && l.size()>0) {
			return l.toArray(new String[1]);
		}
		return null;
	}
	public static List<String> toList(){
		return toList(null);
	}
	public static List<String> toList(ServiceBinding serviceBinding){		
		List<String> res = new ArrayList<>();
		for (TipoTrasformazione tmp : values()) {
			if(serviceBinding!=null && tmp.serviceBinding!=null && !serviceBinding.equals(tmp.serviceBinding)) {
				continue;
			}
			res.add(tmp.getValue());
		}
		return res;
	}	
	
	
	public static String[] toLabelArray(boolean excludeBinaries){
		return toLabelArray(null, excludeBinaries);
	}
	public static String[] toLabelArray(ServiceBinding serviceBinding, boolean excludeBinaries){
		List<String> l = toLabelList(serviceBinding, excludeBinaries);
		if(l!=null && l.size()>0) {
			return l.toArray(new String[1]);
		}
		return null;
	}
	public static List<String> toLabelList(boolean excludeBinaries){
		return toLabelList(null, excludeBinaries);
	}
	public static List<String> toLabelList(ServiceBinding serviceBinding, boolean excludeBinaries){		
		List<String> res = new ArrayList<>();
		for (TipoTrasformazione tmp : values()) {
			if(serviceBinding!=null && tmp.serviceBinding!=null && !serviceBinding.equals(tmp.serviceBinding)) {
				continue;
			}
			if(excludeBinaries && tmp.isBinaryMessage()) {
				continue;
			}
			res.add(tmp.getLabel(serviceBinding));
		}
		return res;
	}
	
	public static String[] toStringArray(boolean excludeBinaries){
		return toStringArray(null, excludeBinaries);
	}
	public static String[] toStringArray(ServiceBinding serviceBinding, boolean excludeBinaries){
		List<String> l = toStringList(serviceBinding, excludeBinaries);
		if(l!=null && l.size()>0) {
			return l.toArray(new String[1]);
		}
		return null;
	}
	public static List<String> toStringList(boolean excludeBinaries){
		return toStringList(null, excludeBinaries);
	}
	public static List<String> toStringList(ServiceBinding serviceBinding, boolean excludeBinaries){		
		List<String> res = new ArrayList<>();
		for (TipoTrasformazione tmp : values()) {
			if(serviceBinding!=null && tmp.serviceBinding!=null && !serviceBinding.equals(tmp.serviceBinding)) {
				continue;
			}
			if(excludeBinaries && tmp.isBinaryMessage()) {
				continue;
			}
			res.add(tmp.toString());
		}
		return res;
	}
	
	
	public static String[] toEnumNameArray(){
		return toEnumNameArray(null);
	}
	public static String[] toEnumNameArray(ServiceBinding serviceBinding){
		List<String> l = toEnumNameList(serviceBinding);
		if(l!=null && l.size()>0) {
			return l.toArray(new String[1]);
		}
		return null;
	}
	public static List<String> toEnumNameList(){
		return toEnumNameList(null);
	}
	public static List<String> toEnumNameList(ServiceBinding serviceBinding){		
		List<String> res = new ArrayList<>();
		for (TipoTrasformazione tmp : values()) {
			if(serviceBinding!=null && tmp.serviceBinding!=null && !serviceBinding.equals(tmp.serviceBinding)) {
				continue;
			}
			res.add(tmp.name());
		}
		return res;
	}	
	

	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoTrasformazione toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoTrasformazione toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoTrasformazione res = null;
		for (TipoTrasformazione tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		try{
			return toEnumConstantFromString(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static IEnumeration toEnumConstantFromString(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoTrasformazione res = null;
		for (TipoTrasformazione tmp : values()) {
			if(tmp.toString().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
}
