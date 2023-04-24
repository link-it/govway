/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.xml2json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class AbstractMappedConverter {

	protected boolean camelCase = false;
	protected boolean camelCase_firstLower = false;
	protected SortedMap<String> renameFields = new SortedMap<String>();
	
	protected List<String> arrays = new ArrayList<>();

	protected SortedMap<String[]> reorderChildren = new SortedMap<String[]>();
	protected boolean forceReorder = false;
	
	protected boolean prettyPrint = false;
	
	
	// -- CamelCase e Rename Field
	
	public void activeCamelCase(boolean firstLowerCase) {
		this.camelCase = true;
		this.camelCase_firstLower = firstLowerCase;
	}
	public void addRenameField(String path, String newName) throws UtilsException {
		this.renameFields.add(path, newName);
	}	
	public void readRenameFieldConfigFromFile(String path) throws UtilsException {
		this.readRenameFieldConfigFromFile(new File(path));
	}
	public void readRenameFieldConfigFromFile(File path) throws UtilsException {
		if(path.exists()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] not exists");
		}
		if(path.canRead()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] cannot read");
		}
		byte [] r = null;
		try {
			r = FileSystemUtilities.readBytesFromFile(path);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		this.readRenameFieldConfigFromFile(r);
	}
	public void readRenameFieldConfigFromFile(byte[] resource) throws UtilsException {
		readFileConfig(resource, this.renameFields);
	}	
	
	
	// -- Array
	
	public void addArrayMapping(String path) throws UtilsException {
		this.arrays.add(path);
	}
	
	public void readArrayMappingFromFile(String path) throws UtilsException {
		this.readArrayMappingFromFile(new File(path));
	}
	public void readArrayMappingFromFile(File path) throws UtilsException {
		if(path.exists()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] not exists");
		}
		if(path.canRead()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] cannot read");
		}
		byte [] r = null;
		try {
			r = FileSystemUtilities.readBytesFromFile(path);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		this.readArrayMappingFromFile(r);
	}
	public void readArrayMappingFromFile(byte[] resource) throws UtilsException {
		readFileConfig(resource, this.arrays);
	}	
			
	
	// -- Reorder elements
	
	public void addReorderChildren(String path, List<String> children) throws UtilsException {
		if(children==null || children.size()<=0) {
			throw new UtilsException("Children undefined");
		}
		addReorderChildren(path, children.toArray(new String[1]));
	}
	public void addReorderChildren(String path, String children) throws UtilsException {
		List<String> l = toList(children);
		if(l.isEmpty()) {
			throw new UtilsException("Children not found");
		}
		addReorderChildren(path, l);
	}
	public void addReorderChildren(String path, String ... children) throws UtilsException {
		if(children==null || children.length<=0) {
			throw new UtilsException("Children undefined");
		}
		this.reorderChildren.add(path, children);
	}	
	public void readReorderChildrenConfigFromFile(String path) throws UtilsException {
		this.readReorderChildrenConfigFromFile(new File(path));
	}
	public void readReorderChildrenConfigFromFile(File path) throws UtilsException {
		if(path.exists()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] not exists");
		}
		if(path.canRead()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] cannot read");
		}
		byte [] r = null;
		try {
			r = FileSystemUtilities.readBytesFromFile(path);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		this.readReorderChildrenConfigFromFile(r);
	}
	public void readReorderChildrenConfigFromFile(byte[] resource) throws UtilsException {
		readFileConfig_mapStringArray(resource, this.reorderChildren);
	}	
	
	public void setForceReorder(boolean forceReorder) {
		this.forceReorder = forceReorder;
	}
	
	
	// -- Pretty Print
	
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	
	
	
	
	
	// -- Utilities interne
	
	protected static String getParentPath(String name) throws UtilsException {
		if(name.contains(".")) {
			if(name.startsWith(".") || name.endsWith(".")) {
				throw new UtilsException("Uncorrect format path ["+name+"]");
			}
			int l = name.lastIndexOf(".");
			String parentPath = name.substring(0, l);
			return parentPath;
		}
		return null;
	}
	protected static String getLastNamePath(String name) throws UtilsException {
		if(name.contains(".")) {
			if(name.startsWith(".") || name.endsWith(".")) {
				throw new UtilsException("Uncorrect format path ["+name+"]");
			}
			int l = name.lastIndexOf(".");
			String last = name.substring(l+1,name.length());
			return last;
		}
		return name;
	}
	
	protected List<String> correctPath(String tipologia, List<String> params) throws UtilsException {
		List<String> newListArray = new ArrayList<>();
		List<String> removePath = new ArrayList<>();
		if(!params.isEmpty()) {
			
			for (String path : params) {
				String [] tmp = path.split("\\.");
				StringBuilder sbNewPath = new StringBuilder();
				int limit = 1;
				
				while(limit<=tmp.length) {
						
					StringBuilder sbPathCheck = new StringBuilder();
					for (int i = 0; i < limit; i++) {
						if(i>0) {
							sbPathCheck.append(".");
						}
						sbPathCheck.append(tmp[i]);
					}
					String patchCheck = sbPathCheck.toString();
					if(sbNewPath.length()>0) {
						sbNewPath.append(".");
					}
					String nome = tmp[limit-1];
					if(this.renameFields.containsKey(patchCheck)) {
						nome = this.renameFields.get(patchCheck);
					}
					sbNewPath.append(nome);
											
					limit++;
				}
				String newPath = sbNewPath.toString();
				//System.out.println("@"+tipologia+" PRIMA path["+path+"] DOPO path["+newPath+"]");
				newListArray.add(newPath);
				if("reorder".equals(tipologia)) {
					removePath.add(path);
				}
			}
		}
		
		if("reorder".equals(tipologia) && !newListArray.isEmpty()) {
			for (int i = 0; i < newListArray.size(); i++) {
				String newPath = newListArray.get(i);
				String path = removePath.get(i);
				String [] v = this.reorderChildren.remove(path);
				String [] newV = new String[v.length];
				for (int j = 0; j < v.length; j++) {
					String old = v[j];
					String key = ("".equals(path)) ? old : path+"."+old;
					String newK = old;
					if(this.renameFields.containsKey(key)) {
						newK = this.renameFields.get(key);
					}
					newV[j] = newK;
				}
				//System.out.println("@"+tipologia+" PRIMA path["+path+"] DOPO path["+newPath+"] ARRAY-PRIMA["+Arrays.asList(v)+"] ARRAY["+Arrays.asList(newV)+"]");
				this.reorderChildren.add(newPath, newV);
			}
		}
				
		return newListArray;
	}
	
	protected void readFileConfig(byte [] file, List<String> list) throws UtilsException {
		_readFileConfig(file, false, list, null, null);
	}
	protected void readFileConfig_mapStringArray(byte [] file, SortedMap<String[]> mapStringArray) throws UtilsException {
		_readFileConfig(file, true, null, mapStringArray, null);
	}
	protected void readFileConfig(byte [] file, SortedMap<String> mapString) throws UtilsException {
		_readFileConfig(file, true, null, null, mapString);
	}
	private void _readFileConfig(byte [] file, boolean isProperty, List<String> listString, SortedMap<String[]> mapStringArray, SortedMap<String> mapString) throws UtilsException {
		Scanner scanner = new Scanner(new String(file));
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line==null || line.trim().equals("") || line.trim().startsWith("#")) {
					continue;
				}
				if(isProperty) {
					if(line.contains("=")==false) {
						throw new UtilsException("In ogni riga deve essere indicata una coppia (nome=valore); non è stato riscontrato il carattere separatore '=' nella riga '"+line+"'");
					}
					int indexOf = line.indexOf("=");
					String key = null;
					String value = null;
					if(indexOf==0) {
						// senza chiave (es. per root)
						key = "";
						value=line.substring(indexOf+1, line.length());
					}
					else if(indexOf==(line.length()-1)) {
						// senza valore
						key=line.substring(0,indexOf);
						value="";
					}
					else {
						key = line.substring(0, indexOf);
						value = line.substring(indexOf+1, line.length());
					}
					
					if(value.contains("#")) {
						// commento inserito dopo la definizione
						int indexOfCommento = value.indexOf("#");
						value = value.substring(0, indexOfCommento);
					}
					
					if(mapStringArray!=null) {
						List<String> l = toList(value);
						if(l.isEmpty()) {
							throw new UtilsException("element for key '"+key+"' not found");
						}
						//System.out.println("INIT ["+key+"] ["+l+"]");
						mapStringArray.add(key, l.toArray(new String[1]));
					}
					else {
						mapString.add(key, value);
					}
				}
				else {
					listString.add(line);
				}
			}
		}finally {
			scanner.close();
		}
	}
	
	protected List<String> toList(String param){
		List<String> l = new ArrayList<>();
		if(param!=null && param.contains(",")) {
			String [] tmp = param.split(",");
			if(tmp!=null && tmp.length>0) {
				for (String c : tmp) {
					if(c!=null && !StringUtils.isEmpty(c.trim())) {
						l.add(c.trim());
					}
				}
			}
		}
		else {
			if(param!=null && !StringUtils.isEmpty(param.trim())) {
				l.add(param.trim());
			}
		}
		return l;
	}
}
