/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.protocol.sdk.archive;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;

/**
 * MappingTypesExtensions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MappingModeTypesExtensions {

	private Hashtable<String, List<ArchiveModeType>> mappingExtensionsTypes = new Hashtable<String, List<ArchiveModeType>>();
	private Hashtable<String, ArchiveType> mappingExtensionsArchiveType = new Hashtable<String, ArchiveType>();
	private List<String> exts = new ArrayList<String>(); // per garantire l'ordine di inserimento
	
	public void add(String ext,ArchiveModeType type) throws ProtocolException{
		this.add(ext, null, type);
	}
	public void add(String ext,ArchiveType archiveType,ArchiveModeType type) throws ProtocolException{
		List<ArchiveModeType> l = new ArrayList<ArchiveModeType>();
		l.add(type);
		this.add(ext, archiveType, l);
	}
	public void add(String ext,ArchiveModeType ... types) throws ProtocolException{
		this.add(ext, null, types);
	}
	public void add(String ext,ArchiveType archiveType,ArchiveModeType ... types) throws ProtocolException{
		List<ArchiveModeType> l = new ArrayList<ArchiveModeType>();
		for (int i = 0; i < types.length; i++) {
			l.add(types[i]);
		}
		this.add(ext, archiveType, l);
	}
	public void add(String ext,List<ArchiveModeType> listTypes) throws ProtocolException{
		this.add(ext, null, listTypes);
	}
	public void add(String ext,ArchiveType archiveType, List<ArchiveModeType> listTypes) throws ProtocolException{
		List<ArchiveModeType> l = null;
		if(this.mappingExtensionsTypes.containsKey(ext)){
			l = this.mappingExtensionsTypes.remove(ext);
		}else{
			l = new ArrayList<ArchiveModeType>();
		}
		
		for (ArchiveModeType type : listTypes) {
			
			if(l.contains(type)){
				throw new ProtocolException("Type["+type+"] already exists for extension ["+ext+"]");
			}
			
			// Verificare che cmq il tipo non sia assegnato ad un altra extension!
			// Questo DEVE essere permesso. Ad esempio uno zip gestisce diversi formati piu' specifici
//			String extAlreadyExist = this.mappingTypeToExt(type);
//			if(extAlreadyExist!=null){
//				throw new ProtocolException("Type["+type+"] already exists for other extension ["+extAlreadyExist+"]");
//			}
			
			l.add(type);
		}
		this.mappingExtensionsTypes.put(ext, l);
		if(archiveType!=null){
			this.mappingExtensionsArchiveType.put(ext, archiveType);
		}
		if(this.exts.contains(ext)==false){
			this.exts.add(ext);
		}
	}
	
	public String mappingTypeToFirstExt(ArchiveModeType type){
		// Viene ritornato il tipo al livello 0
		return mappingTypeToExt(type, 0);
	}
	public String mappingTypeToExt(ArchiveModeType type,int index){
		List<String> l = this.mappingTypeToExts(type);
		if(l.size()>=(index+1)){
			return l.get(index); 
		}else{
			return null;
		}
	}
	public List<String> mappingTypeToExts(ArchiveModeType type){
		List<String> exts = new ArrayList<String>();
		for (int i = 0; i < this.exts.size(); i++) {
			String ext = this.exts.get(i);
			List<ArchiveModeType> types = this.mappingExtensionsTypes.get(ext);
			if(types.contains(type)){
				exts.add(ext);
			}
		}
		return exts;
	}
	
	public ArchiveModeType mappingExtToFirstType(String ext){
		// Viene ritornato il tipo al livello 0		
		return this.mappingExtToType(ext, 0);  
	}
	public ArchiveModeType mappingExtToType(String ext,int index){
		if(this.mappingExtensionsTypes.containsKey(ext)){
			List<ArchiveModeType> l = this.mappingExtensionsTypes.get(ext);
			if(l.size()>=(index+1)){
				return l.get(index);
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	public List<ArchiveModeType> mappingExtToTypes(String ext){
		if(this.mappingExtensionsTypes.containsKey(ext)){
			return this.mappingExtensionsTypes.get(ext); 			
		}else{
			return null;
		}
	}
	
	public List<String> getExtensions(){
		return this.exts;
	}
	
	public List<ArchiveModeType> getTypes(String ext){
		return this.mappingExtensionsTypes.get(ext);
	}
	
	public List<ArchiveModeType> getAllTypes(){
		
		List<ArchiveModeType> typesAll = new ArrayList<ArchiveModeType>();
		List<String> exts = this.getExtensions();
		for (int i = 0; i < exts.size(); i++) {
			List<ArchiveModeType> types = this.getTypes(exts.get(i));
			for (ArchiveModeType type : types) {
				if(typesAll.contains(type)==false){
					typesAll.add(type);
				}
			}
		}
		return typesAll;
	}
	
	public String mappingArchiveTypeToExt(ArchiveType type){
		
		Enumeration<String> exts = this.mappingExtensionsArchiveType.keys();
		while (exts.hasMoreElements()) {
			String ext = (String) exts.nextElement();
			ArchiveType check = this.mappingExtensionsArchiveType.get(ext);
			if(check.equals(type)){
				return ext;
			}
		}			
		
		return null;
	}
}
