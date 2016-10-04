/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.pdd.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.io.JarUtilities;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* GeneralInstanceProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/

public class GeneralInstanceProperties {

	private File archiveFile = null;
	
	private int RESULT_LENGTH = 8;
	
	
	public Object[] reads(Logger log){
		
		
		// Properties
		Object[] oR = readLoadProperties(log);
		if(oR!=null){
			return oR;
		}
		
		
		// La ricerca viene attivata SOLO se la proprieta' e' presente e assume il valore true
		boolean lookup = false;
		
		String properties = System.getenv(CostantiPdD.OPENSPCOOP2_LOOKUP);
		if(properties!=null && "true".equalsIgnoreCase(properties)){
			lookup = true;
		}
		
		if(properties==null){
			properties = System.getProperty(CostantiPdD.OPENSPCOOP2_LOOKUP);
			if(properties!=null && "true".equalsIgnoreCase(properties)){
				lookup = true;
			}
		}
		
		if(!lookup){
			return null;
		}		
		
		// Lookup
		File fClasspath = null;
		try{
			URL urlClasspath = GeneralInstanceProperties.class.getResource("/openspcoop2.log4j2.properties");
			if(urlClasspath!=null){
				URI uri = urlClasspath.toURI();
				String uriS = uri.toString();
				if(uriS.startsWith("vfsfile:")){
					// jboss 5
					uriS = uriS.substring("vfsfile:".length());
				}
				else if(uriS.startsWith("vfszip:")){
					// jboss 5
					uriS = uriS.substring("vfszip:".length());
				}
				else if(uriS.contains(":")){
					// jboss 4
					String [] tmp = uriS.split(":");
					if(tmp.length>1){
						uriS = tmp[1].trim();
					}
				}
				File f = (new File(uriS)).getParentFile();
				if(f!=null){					
					// OpenSPCoop.ear/properties/openspcoop2.log4j2.properties
					// openspcoop.war/WEB-INF/classes/openspcoop2.log4j2.properties
					if("properties".equals(f.getName())){
						fClasspath = f.getParentFile().getParentFile();
					}else if("classes".equals(f.getName())){
						fClasspath = f.getParentFile().getParentFile().getParentFile();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(fClasspath!=null){
			
			Vector<File> files = new Vector<File>();
			findArchives(log, fClasspath, files);
						
			if(files.size()<=0){
				
				// JBOSS
				String dir = fClasspath.getName();
				fClasspath = fClasspath.getParentFile();
				if(fClasspath!=null){
					if("tmp".equalsIgnoreCase(fClasspath.getName())){
						fClasspath = fClasspath.getParentFile();
						if(fClasspath!=null){
							fClasspath = new File(fClasspath,dir);
							findArchives(log, fClasspath, files);
						}
					}
					else if("deploy".equalsIgnoreCase(dir)){
						if(fClasspath!=null){
							fClasspath = new File(fClasspath,"tmp");
							if(fClasspath.exists() && fClasspath.canRead()){
								fClasspath = new File(fClasspath,"deploy");
								if(fClasspath.exists() && fClasspath.canRead()){
									findArchives(log, fClasspath, files);
								}
							}
						}
					}
				}
				
			}
			
			for (int i = 0; i < files.size(); i++) {
				Object[] o = null;
				this.archiveFile = files.get(i);
				if(this.archiveFile.isFile()){
					log.debug("Archive ["+this.archiveFile.getAbsolutePath()+"] find in classpath");
					o = readFile(this.archiveFile,log);
				}
				else{
					log.debug("Directory ["+this.archiveFile.getAbsolutePath()+"] find in classpath");
					o = readDir(this.archiveFile,log);
				}
				if(o!=null){
					return o;
				}
			}
			
		}
		
		
		
		return null;
	}
	
	private Object[] readLoadProperties(Logger log){
		try{
			
			String loaderP = System.getenv(CostantiPdD.OPENSPCOOP2_LOADER);
			if(loaderP==null){
				loaderP = System.getProperty(CostantiPdD.OPENSPCOOP2_LOADER);
			}
			if(loaderP==null){
				InputStream is = GeneralInstanceProperties.class.getResourceAsStream("/op2loader.properties");
				if(is!=null){
					try{
						Properties p = new Properties();
						p.load(is);
						loaderP = p.getProperty("loader");
						if(loaderP!=null){
							loaderP=loaderP.trim();
						}
					}finally{
						try{
							is.close();
						}catch(Exception eClose){}
					}
				}
			}
			if(loaderP == null){
				return null;
			}
			
			// Loader
			Method method = this.getClass().getClassLoader().getClass().getMethod("loadClass", String.class);
			Object c = method.invoke(this.getClass().getClassLoader(), loaderP);
			
			java.lang.ClassLoader loader = null;
			if(c!=null){
				Constructor<?> constructor = ((Class<?>)c).getConstructor(java.lang.ClassLoader.class);
				loader = (java.lang.ClassLoader) constructor.newInstance(this.getClass().getClassLoader());
			}
			if(loader==null){
				throw new Exception("Loader ["+loaderP+"] non caricato");
			}
			
			// Metodo per properties
			Method methodProperties = null;
			try{
				methodProperties = loader.getClass().getMethod("getClassProperties");
			}catch(Exception e){
			}catch(Throwable e){}
			if(methodProperties!=null){
				Object result = methodProperties.invoke(loader);
				if(result!=null && (result instanceof String) ){
					Object[] o = readProperties(log, ((String)result), loader, "getClassProperties");
					if(o!=null){
						return o;
					}
				}
			}
			
			// Proprieta' inserite nel sistema
			String tipo = "systemProperties";
			String properties = System.getenv(CostantiPdD.OPENSPCOOP2_LOADER_PROPERTIES);
			if(properties==null){
				tipo = "javaProperties";
				properties = System.getProperty(CostantiPdD.OPENSPCOOP2_LOADER_PROPERTIES);
			}
			if(properties == null){
				throw new Exception("Trovata proprieta' per loader ma non sono state fornite le properties");
			}
			
			Object[] o = readProperties(log, properties, loader, tipo);
			if(o!=null){
				return o;
			}
			
			throw new Exception("Trovata proprieta' per loader ma non sono state fornite correttamente le properties");
			
		}catch(Exception e){
			e.printStackTrace(System.out);
			log.debug("LoadProperties ERROR",e);
		}
		catch(Throwable e){
			e.printStackTrace(System.out);
			log.debug("LoadProperties ERROR",e);
		}
		
		return null;
	}
	
	private Object[] readProperties(Logger log,String properties,java.lang.ClassLoader loader,String tipoRicerca) throws Exception{
		String [] split = null;
		if(properties.contains(",")){
			split = properties.split(",");
		}else if(properties.contains(":")){
			split = properties.split(":");
		}else if(properties.contains(";")){
			split = properties.split(";");
		}else if(properties.contains(" ")){
			split = properties.split(" ");
		}else{
			throw new Exception("Trovata proprieta' per loader ma non sono state fornite correttamente le properties");
		}
		if(split==null || split.length!=(this.RESULT_LENGTH-1)){
			throw new Exception("Trovata proprieta' per loader ma non sono state fornite correttamente le properties");
		}
		
		// Verifico properties
		Properties opP = null;
		Properties clP = null;
		Properties pddP = null;
		Properties configP = null;
		Properties logP = null;
		Properties msgDiagP = null;
		Properties cacheP = null;
		for (int i = 0; i < split.length; i++) {
			String v = split[i].trim();
			try{
				//System.out.println("V["+v+"]");
				//System.out.println("CLASS["+this.getClass().getName()+"]");
				//System.out.println("CLASS2["+this.getClass().getClassLoader().getClass().getName()+"]");
				//Method method = this.getClass().getClassLoader().getClass().getMethod("loadClass", String.class);
				Method method = ClassLoader.class.getMethod("loadClass", String.class);
				//System.out.println("AfterMethod");
				Object find = method.invoke(this.getClass().getClassLoader(), ClassLoader.class.getName());
				//System.out.println("Find ["+find+"]");
				//if(find!=null){
				//	System.out.println("Find ["+find.getClass().getName()+"]");
				//}
				//System.out.println("LOADER ["+loader.getClass().getName()+"]");
				find = method.invoke(loader, Properties.class.getName());
				//System.out.println("PropInterface ["+find+"]");
				//if(find!=null){
				//	System.out.println("PropInterface ["+find.getClass().getName()+"]");
				//}
				Object c = method.invoke(loader, v);
				if(c==null){
					throw new Exception("Proprieta' ["+v+"] non trovata");
				}
				if(((Class<?>)find).isAssignableFrom(((Class<?>)c))){
					Constructor<?> constructor = ((Class<?>)c).getConstructor();
					Properties p = (Properties) constructor.newInstance();
					Object type = p.get("type");
					if(type==null){
						throw new Exception("Proprieta' ["+v+"] con type non definito");
					}
					if("openspcoop".equals(type)){
						opP = p;
					}
					else if("className".equals(type)){
						clP = p;
					}
					else if("pdd".equals(type)){
						pddP = p;
					}
					else if("logger".equals(type)){
						logP = p;
					}
					else if("config".equals(type)){
						configP = p;
					}
					else if("msgDiagnostici".equals(type)){
						msgDiagP = p;
					}
					else if("cache".equals(type)){
						cacheP = p;
					}
					else{
						throw new Exception("Proprieta' ["+v+"] con type non corretto");
					}
				}else{
					throw new Exception("Proprieta' ["+v+"] di tipo non corretto");
				}
			}catch(Exception e){
				throw new Exception("Trovata proprieta' per loader ma non sono state fornite correttamente le properties. Errore durante la lookup della properties ["+v+"]: "+e.getMessage(),e);
			}catch(Throwable e){
				throw new Exception("Trovata proprieta' per loader ma non sono state fornite correttamente le properties. Errore durante la lookup della properties ["+v+"]: "+e.getMessage(),e);
			}
		}
		if(opP!=null && clP!=null && pddP!=null && logP!=null && configP!=null && msgDiagP!=null && cacheP!=null){
			Object [] o = new Object[this.RESULT_LENGTH];
			o[0]=loader;
			o[1]=opP;
			o[2]=clP;
			o[3]=pddP;
			o[4]=logP;
			o[5]=configP;
			o[6]=msgDiagP;
			o[7]=cacheP;
			for (int i = 0; i < o.length; i++) {
				log.debug("Class ("+tipoRicerca+") ["+i+"]=["+o[i].getClass().getName()+"]");
			}
			log.debug("Loader find ("+tipoRicerca+")");
			return o;
		}
		
		return null;
	}
	
	private void findArchives(Logger log,File fClasspath,Vector<File> files){
		log.debug("Search libraries in classpath ["+fClasspath.getAbsolutePath()+"] ...");
		
		if(fClasspath.isFile()){
			log.debug("Classpath is file");
			return;
		}
		if(fClasspath.canRead()==false){
			log.debug("Classpath is not readable");
			return;
		}
		
		File [] f = fClasspath.listFiles();
		if(f!=null){
			for (int i = 0; i < f.length; i++) {
				
				if(f[i].canRead()==false){
					continue;
				}
				if(f[i].isFile()){
					if(checkFile(f[i])){
						log.debug("Find archive ["+f[i].getAbsolutePath()+"]");
						files.add(f[i]);
					}
				}
				else if(f[i].isDirectory()){
					if(checkDir(f[i])){
						log.debug("Find dir ["+f[i].getAbsolutePath()+"]");
						files.add(f[i]);
					}
				}
			}
		}
		log.debug("Search libraries in classpath ["+fClasspath.getAbsolutePath()+"]: find "+files.size());
	}
	
	private boolean checkFile(File file)
	{
		if (!file.exists ()) 
			return false;
		if (!file.canRead()) 
			return false;
		if (file.isDirectory()) 
			return false;
		try{
			JarFile archive = JarUtilities.getJar(file, false); 
			if(archive==null){
				return false;
			}
			Manifest manifest = archive.getManifest();
			return checkManifest(manifest);
			
		}catch(Exception e){
			return false;
		}
		
	}
	private boolean checkDir(File dir){
		
		try{
		
			if(!dir.canRead()){
				return false;
			}
			File [] childs = dir.listFiles();
			if(childs==null || childs.length<=0){
				return false;
			}
			for (int i = 0; i < childs.length; i++) {
				if(!childs[i].canRead()){
					continue;
				}
				if(!childs[i].isDirectory()){
					continue;
				}
				if("META-INF".equals(childs[i].getName())==false){
					continue;
				}
				File [] childsInterni = childs[i].listFiles();
				if(childsInterni==null || childsInterni.length<=0){
					return false;
				}
				for (int j = 0; j < childsInterni.length; j++) {
					if(!childsInterni[j].canRead()){
						continue;
					}
					if(!childsInterni[j].isFile()){
						continue;
					}
					if("MANIFEST.MF".equals(childsInterni[j].getName())==false){
						continue;
					}
					byte[] m = FileSystemUtilities.readBytesFromFile(childsInterni[j]);
					if(m==null){
						return false;
					}
					Manifest manifest = new Manifest(new ByteArrayInputStream(m));
					return checkManifest(manifest);
				}
			}
			
			return false;
			
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean checkManifest(Manifest manifest){
		if(manifest==null){
			return false;
		}
		if(manifest.getMainAttributes()==null){
			return false;
		}
		
		Attributes attributes = manifest.getMainAttributes();
		if(attributes.size()<2){
			return false;
		}
		
		String attrProduct = attributes.getValue("product-name");
		if(CostantiPdD.OPENSPCOOP2.equals(attrProduct)==false){
			return false;
		}
		
		String attrCopyright = attributes.getValue("copyright");
		if(attrCopyright==null){
			return false;
		}
		if(attrCopyright.contains("Link.it srl (http://link.it). All rights reserved.")==false){
			return false;
		}
		return true;
	}
	
	
	
	private Object[] readFile(File file,Logger log)
	{
		if (!file.exists ()){
			log.debug("File ["+file.getAbsolutePath()+"] not exist");
			return null;
		}
		if (!file.canRead()) {
			log.debug("File ["+file.getAbsolutePath()+"] not readable");
			return null;
		}
		if (file.isDirectory()) {
			log.debug("File ["+file.getAbsolutePath()+"] is directory");
			return null;
		}
		try{
			JarFile archive = JarUtilities.getJar(file, false); 
			if(archive==null){
				log.debug("File ["+file.getAbsolutePath()+"] is not jar archive");
				return null;
			}
			
			Enumeration<JarEntry> entries = null;
			java.lang.ClassLoader loader = null;
			String parentDirLoader = null;
			
			// Class
			entries = archive.entries();
			while (entries.hasMoreElements()){
				JarEntry entry = entries.nextElement ();
				String extension = "";
				if( entry.getName().contains(".")){
					extension = entry.getName().substring(entry.getName().lastIndexOf('.')+1, entry.getName().length());
				}
				String nome = entry.getName ().replaceAll("/", ".");
				if(extension!=null){
					extension = "."+extension;
					nome = nome.substring(0,nome.length()-extension.length());
				}
				
				if(nome.endsWith(".")){
					// directory
					continue;
				}
			
				try{
					Method method = this.getClass().getClassLoader().getClass().getMethod("loadClass", String.class);
					Object find = method.invoke(this.getClass().getClassLoader(), ClassLoader.class.getName());
					Object c = null;
					try{
						c = method.invoke(this.getClass().getClassLoader(), nome);
					}catch(Exception e){}
					catch(Throwable e){}
					if(c!=null){
						if(((Class<?>)find).isAssignableFrom(((Class<?>)c))){
							Constructor<?> constructor = ((Class<?>)c).getConstructor(java.lang.ClassLoader.class);
							loader = (java.lang.ClassLoader) constructor.newInstance(this.getClass().getClassLoader());
							parentDirLoader = nome.split("\\.")[0].trim()+".";
							break;
						}
					}
				}catch(Exception e){}
			}
			if(loader!=null){
				log.debug("Loader find (loadClass)");
			}
			
			// Bytes
			if(loader==null){
				entries = archive.entries();
				while (entries.hasMoreElements()){
					JarEntry entry = entries.nextElement ();
					String extension = "";
					if( entry.getName().contains(".")){
						extension = entry.getName().substring(entry.getName().lastIndexOf('.')+1, entry.getName().length());
					}
					String nome = entry.getName ().replaceAll("/", ".");
					if(extension!=null){
						extension = "."+extension;
						nome = nome.substring(0,nome.length()-extension.length());
					}
					
					if(nome.endsWith(".")){
						// directory
						continue;
					}
					
					byte[] entryBytes = null;
					try{
						entryBytes = JarUtilities.getEntry(file, entry.getName());
					}catch(Exception e){}
					catch(Throwable e){}
					try{
						ResourceFinder finder = new ResourceFinder(this.getClass().getClassLoader());
						Object c = null;
						try{
							c = finder.loadResource(nome, entryBytes);
						}catch(Exception e){}
						catch(Throwable e){}
						if(c!=null){
							Method method = this.getClass().getClassLoader().getClass().getMethod("loadClass", String.class);
							Object find = method.invoke(this.getClass().getClassLoader(), ClassLoader.class.getName());
							if(((Class<?>)find).isAssignableFrom(((Class<?>)c))){
								Constructor<?> constructor = ((Class<?>)c).getConstructor(java.lang.ClassLoader.class,JarFile.class,String.class, File.class);
								parentDirLoader = nome.split("\\.")[0].trim()+".";
								loader = (java.lang.ClassLoader) constructor.newInstance(this.getClass().getClassLoader(),archive,parentDirLoader,this.archiveFile);
								break;
							}
						}
					}catch(Exception e){}
				}
				if(loader!=null){
					log.debug("Loader find (ResourceFinder)");
				}
			}
			
			// Jar
			if(loader==null){
				entries = archive.entries();
				while (entries.hasMoreElements()){
					JarEntry entry = entries.nextElement ();
					String extension = "";
					if( entry.getName().contains(".")){
						extension = entry.getName().substring(entry.getName().lastIndexOf('.')+1, entry.getName().length());
					}
					String nome = entry.getName ().replaceAll("/", ".");
					if(extension!=null){
						extension = "."+extension;
						nome = nome.substring(0,nome.length()-extension.length());
					}
					
					if(nome.endsWith(".")){
						// directory
						continue;
					}
					
					byte[] entryBytes = null;
					try{
						entryBytes = JarUtilities.getEntry(file, entry.getName());
					}catch(Exception e){}
					catch(Throwable e){}
					
					if(entryBytes!=null){
						File tmp = null;
						try{							
							tmp = File.createTempFile("PddInterceptor", "PddInterceptor");
							FileSystemUtilities.writeFile(tmp, entryBytes);
							if(JarUtilities.isJar(tmp, false)){
								if(checkFile(tmp)){
									Object [] o = readFile(tmp,log);
									if(o!=null){
										return o;
									}
								}
							}
						}catch(Exception e){}
						catch(Throwable e){}
						finally{
							try{
								FileSystemUtilities.deleteDir(tmp);
								tmp.deleteOnExit();
							}catch(Exception e){}
						}
					}			
				}
			}
			
			// Unzip jar per verifica dir interne
			if(loader==null){
				File tmp = null;
				try{			
					tmp = File.createTempFile("PddInterceptor", "PddInterceptor");
					tmp.delete();
					ZipUtilities.unzipFile(file.getAbsolutePath(), tmp.getAbsolutePath());
					Object [] o = readDir(tmp,log);
					if(o!=null){
						return o;
					}
				}catch(Exception e){}
				catch(Throwable e){}
				finally{
					try{
						FileSystemUtilities.deleteDir(tmp);
						tmp.deleteOnExit();
					}catch(Exception e){}
				}
			}
			
			
			// Loader
			if(loader!=null){
											
				// Verifico properties
				entries = archive.entries();
				Properties opP = null;
				Properties clP = null;
				Properties pddP = null;
				Properties configP = null;
				Properties logP = null;
				Properties msgDiagP = null;
				Properties cacheP = null;
				while (entries.hasMoreElements()){
					JarEntry entry = entries.nextElement ();
					String extension = "";
					if( entry.getName().contains(".")){
						extension = entry.getName().substring(entry.getName().lastIndexOf('.')+1, entry.getName().length());
					}
					String nome = entry.getName ().replaceAll("/", ".");
					if(extension!=null){
						extension = "."+extension;
						nome = nome.substring(0,nome.length()-extension.length());
					}
					if(nome.equals(loader.getClass().getName())==false && !".".equals(extension) && nome.startsWith(parentDirLoader)){
						Object find = null;
						Object c = null;
						try{
							log.debug("Load ["+nome+"]...");
							Method method = loader.getClass().getMethod("loadClass", String.class);
							find = method.invoke(loader, Properties.class.getName());
							c = method.invoke(loader, nome);
							log.debug("Load ["+nome+"] OK");
							if(find==null){
								throw new Exception("find null");
							}
							if(c==null){
								throw new Exception("c null");
							}
						}catch(Exception e){
							log.debug("Load ["+nome+"] ERROR",e);
						}
						catch(Throwable e){
							log.debug("Load ["+nome+"] ERROR",e);
						}
						if(find!=null && c!=null){
							try{
								if(((Class<?>)find).isAssignableFrom(((Class<?>)c))){
									Constructor<?> constructor = ((Class<?>)c).getConstructor();
									Properties properties = (Properties) constructor.newInstance();
									Object type = properties.get("type");
									if(type==null){
										continue;
									}
									else if("openspcoop".equals(type)){
										opP = properties;
									}
									else if("className".equals(type)){
										clP = properties;
									}
									else if("pdd".equals(type)){
										pddP = properties;
									}
									else if("logger".equals(type)){
										logP = properties;
									}
									else if("config".equals(type)){
										configP = properties;
									}
									else if("msgDiagnostici".equals(type)){
										msgDiagP = properties;
									}
									else if("cache".equals(type)){
										cacheP = properties;
									}
								}
							}catch(Exception e){
								log.debug("Loader instance ["+nome+"] error: "+e.getMessage(),e);
							}
							catch(Throwable e){
								log.debug("Loader instance ["+nome+"] error: "+e.getMessage(),e);
							}
						}
					}			
				}
				if(opP!=null && clP!=null && pddP!=null && logP!=null && configP!=null && msgDiagP!=null && cacheP!=null){
					Object [] o = new Object[this.RESULT_LENGTH];
					o[0]=loader;
					o[1]=opP;
					o[2]=clP;
					o[3]=pddP;
					o[4]=logP;
					o[5]=configP;
					o[6]=msgDiagP;
					o[7]=cacheP;
					for (int i = 0; i < o.length; i++) {
						log.debug("Class ["+i+"]=["+o[i].getClass().getName()+"]");
					}
					return o;
				}
			}
			
		}catch(Exception e){
			return null;
		}
		
		return null;
	}
	
		
	private Object[] readDir(File file,Logger log)
	{
		
		if (!file.exists ()) {
			log.debug("File (DIR) ["+file.getAbsolutePath()+"] not exist");
			return null;
		}
		if (!file.canRead()) {
			log.debug("File (DIR) ["+file.getAbsolutePath()+"] not readable");
			return null;
		}
		if (file.isFile()) {
			log.debug("File (DIR) ["+file.getAbsolutePath()+"] is directory");
			return null;
		}
		
		File[] childs = file.listFiles();
		if(childs!=null && childs.length>0){
			
			java.lang.ClassLoader loader = null;
			String parentDirLoader = null;			
			
			// Chiamate ricorsive
			for (int i = 0; i < childs.length; i++) {
				
				if(childs[i].isDirectory()){
					
					// Check if is war/ear
					if(childs[i].listFiles()!=null && childs[i].listFiles().length>0){
						
						File [] childsInterni = childs[i].listFiles();
						File WEB_LIB = null;
						File WEB_CLASSES = null;
						File EAR = null;
						
						if("WEB-INF".equals(childs[i].getName())){
							for (int k = 0; k < childsInterni.length; k++) {
								if("lib".equals(childsInterni[k].getName())){
									WEB_LIB = childsInterni[k];
								}
								if("classes".equals(childsInterni[k].getName())){
									WEB_CLASSES = childsInterni[k];
								}
							}
						}
						else if("META-INF".equals(childs[i].getName())){
							for (int k = 0; k < childsInterni.length; k++) {
								if("application.xml".equals(childsInterni[k].getName())){
									EAR = childs[i];
								}
							}
						}
						
						if(WEB_LIB!=null || WEB_CLASSES!=null || EAR!=null){
							// dir
							if(WEB_LIB!=null){
								Object[] o = null;
								try{
									o = readDir(WEB_LIB,log);
								}catch(Exception e){}
								catch(Throwable e){}
								if(o!=null)
									return o;	
							}
							
							if(WEB_CLASSES!=null){
								Object[] o = null;
								try{
									o = readDir(WEB_CLASSES,log);
								}catch(Exception e){}
								catch(Throwable e){}
								if(o!=null)
									return o;	
							}
							
							if(WEB_LIB!=null || WEB_CLASSES!=null){
								Object[] o = null;
								try{
									o = readDir(childs[i],log);
								}catch(Exception e){}
								catch(Throwable e){}
								if(o!=null)
									return o;	
							}
							
							if(EAR!=null){
								Object[] o = null;
								try{
									o = readDir(EAR,log);
								}catch(Exception e){}
								catch(Throwable e){}
								if(o!=null)
									return o;	
							}
						}
						
						// Provo ad utilizzare direttamente la directory
						Object[] o = null;
						try{
							o = readDir(childs[i],log);
						}catch(Exception e){}
						catch(Throwable e){}
						if(o!=null)
							return o;	
						
					}
				}
			}
			
			Vector<String> entries = new Vector<String>();
			Vector<byte[]> entriesBytes = new Vector<byte[]>();
			
			// Inizializzo vector
			for (int i = 0; i < childs.length; i++) {
				
				if(childs[i].isDirectory()){
					
					// Check if is war/ear
					if(childs[i].listFiles()!=null && childs[i].listFiles().length>0){
			
						// Utilizzo i files
						File [] childsEntries = file.listFiles();
						try{
							if(childsEntries!=null){
								for (int j = 0; j < childsEntries.length; j++) {
									buildEntryNames(childsEntries[j], null, entries,entriesBytes);
								}
							}
						}catch(Exception e){}
					}
				}
				else{
					try{
						entries.add(childs[i].getName());
						entriesBytes.add(FileSystemUtilities.readBytesFromFile(childs[i]));
					}catch(Exception e){}
				}
			}
			
			
			// Class
			for (int k = 0; k < entries.size(); k++) {
					
				String extension = "";
				if( entries.get(k).contains(".")){
					extension = entries.get(k).substring(entries.get(k).lastIndexOf('.')+1, entries.get(k).length());
				}
				String nome = entries.get(k).replaceAll("/", ".");
				if(extension!=null){
					extension = "."+extension;
					nome = nome.substring(0,nome.length()-extension.length());
				}
				
				try{
					Method method = this.getClass().getClassLoader().getClass().getMethod("loadClass", String.class);
					Object find = method.invoke(this.getClass().getClassLoader(), ClassLoader.class.getName());
					Object c = null;
					try{
						c = method.invoke(this.getClass().getClassLoader(), nome);
					}catch(Exception e){}
					catch(Throwable e){}
					if(((Class<?>)find).isAssignableFrom(((Class<?>)c))){
						Constructor<?> constructor = ((Class<?>)c).getConstructor(java.lang.ClassLoader.class);
						loader = (java.lang.ClassLoader) constructor.newInstance(this.getClass().getClassLoader());
						parentDirLoader = nome.split("\\.")[0].trim()+".";
						break;
					}
				}catch(Exception e){}
				catch(Throwable e){}	
			}
			if(loader!=null){
				log.debug("Loader DIR find (loadClass)");
			}
			
			
			
			if(loader==null){
				// Bytes 
				for (int k = 0; k < entries.size(); k++) {
						
					String extension = "";
					if( entries.get(k).contains(".")){
						extension = entries.get(k).substring(entries.get(k).lastIndexOf('.')+1, entries.get(k).length());
					}
					String nome = entries.get(k).replaceAll("/", ".");
					if(extension!=null){
						extension = "."+extension;
						nome = nome.substring(0,nome.length()-extension.length());
					}
					
					try{
						Method method = this.getClass().getClassLoader().getClass().getMethod("loadClass", String.class);
						Object find = method.invoke(this.getClass().getClassLoader(), ClassLoader.class.getName());
						Object c = null;
						try{
							ResourceFinder finder = new ResourceFinder(this.getClass().getClassLoader());
							c = finder.loadResource(nome, entriesBytes.get(k));
						}catch(Exception e){}
						catch(Throwable e){}
						if(c!=null){
							if(((Class<?>)find).isAssignableFrom(((Class<?>)c))){
								Constructor<?> constructor = ((Class<?>)c).getConstructor(java.lang.ClassLoader.class,java.util.Vector.class,java.util.Vector.class,String.class, File.class);
								parentDirLoader = nome.split("\\.")[0].trim()+".";
								loader = (java.lang.ClassLoader) constructor.newInstance(this.getClass().getClassLoader(),entries,entriesBytes,parentDirLoader,this.archiveFile);
								break;
							}
						}
					}catch(Exception e){}
					catch(Throwable e){}
				}
				if(loader!=null){
					log.debug("Loader DIR find (ResourceFinder)");
				}
			}
			
			
			if(loader==null){
				
				// Jar 
				for (int k = 0; k < entries.size(); k++) {
						
					if(entriesBytes.get(k)!=null){
						File tmp = null;
						try{
							tmp = File.createTempFile("PddInterceptor", "PddInterceptor");
							FileSystemUtilities.writeFile(tmp, entriesBytes.get(k));
							if(JarUtilities.isJar(tmp, false)){
								if(checkFile(tmp)){
									Object [] o = readFile(tmp,log);
									if(o!=null){
										return o;
									}
								}
							}
						}catch(Exception e){}
						catch(Throwable e){}
						finally{
							try{
								FileSystemUtilities.deleteDir(tmp);
								tmp.deleteOnExit();
							}catch(Exception e){}
						}
					}		

				}
			}
			
			
			// Loader trovato
			if(loader!=null){
								
				// Verifico properties
				Properties opP = null;
				Properties clP = null;
				Properties pddP = null;
				Properties configP = null;
				Properties logP = null;
				Properties msgDiagP = null;
				Properties cacheP = null;
				for (int k = 0; k < entries.size(); k++) {
					
					String entryName = entries.get(k);
					String extension = "";
					if( entryName.contains(".")){
						extension = entryName.substring(entryName.lastIndexOf('.')+1, entryName.length());
					}
					String nome = entryName.replaceAll("/", ".");
					if(extension!=null){
						extension = "."+extension;
						nome = nome.substring(0,nome.length()-extension.length());
					}
					if(nome.equals(loader.getClass().getName())==false && !".".equals(extension) && nome.startsWith(parentDirLoader) ){
						Object find = null;
						Object c = null;
						//Throwable cE = null;
						try{
							log.debug("Load (DIR) ["+nome+"]...");
							Method method = loader.getClass().getMethod("loadClass", String.class);
							find = method.invoke(loader, Properties.class.getName());
							c = method.invoke(loader, nome);
							log.debug("Load (DIR) ["+nome+"] OK");
							if(find==null){
								throw new Exception("find null");
							}
							if(c==null){
								throw new Exception("c null");
							}
						}catch(Exception e){
							log.debug("Loader (DIR) instance ["+nome+"] error: "+e.getMessage(),e);
						}
						catch(Throwable e){
							log.debug("Loader (DIR) instance ["+nome+"] error: "+e.getMessage(),e);
						}
						if(find!=null && c!=null){
							try{
								if(((Class<?>)find).isAssignableFrom(((Class<?>)c))){
									Constructor<?> constructor = ((Class<?>)c).getConstructor();
									Properties properties = (Properties) constructor.newInstance();
									Object type = properties.get("type");
									if(type==null){
										continue;
									}
									else if("openspcoop".equals(type)){
										opP = properties;
									}
									else if("className".equals(type)){
										clP = properties;
									}
									else if("pdd".equals(type)){
										pddP = properties;
									}
									else if("logger".equals(type)){
										logP = properties;
									}
									else if("config".equals(type)){
										configP = properties;
									}
									else if("msgDiagnostici".equals(type)){
										msgDiagP = properties;
									}
									else if("cache".equals(type)){
										cacheP = properties;
									}
								}
							}catch(Exception e){
								log.debug("Loader (DIR) instance ["+nome+"] error: "+e.getMessage(),e);
							}
							catch(Throwable e){
								log.debug("Loader (DIR) instance ["+nome+"] error: "+e.getMessage(),e);
							}
						}
					}			
				}
				if(opP!=null && clP!=null && pddP!=null && logP!=null && configP!=null && msgDiagP!=null && cacheP!=null){
					Object [] o = new Object[this.RESULT_LENGTH];
					o[0]=loader;
					o[1]=opP;
					o[2]=clP;
					o[3]=pddP;
					o[4]=logP;
					o[5]=configP;
					o[6]=msgDiagP;
					o[7]=cacheP;
					for (int i = 0; i < o.length; i++) {
						log.debug("Class (DIR) ["+i+"]=["+o[i].getClass().getName()+"]");
					}
					return o;
				}
			}
		}
		
		return null;
	}
	
	private void buildEntryNames(File file,String prefix,Vector<String> files,Vector<byte[]> entriesBytes) throws Exception{
		
		String name = null;
		if(prefix!=null){
			name = prefix+"."+file.getName();
		}
		else{
			name = file.getName();
		}
		
		if(file.isFile()){
			files.add(name);
			entriesBytes.add(FileSystemUtilities.readBytesFromFile(file));
			return;
		}
		else if(file.isDirectory()){
			File [] childs = file.listFiles();
			if(childs!=null){
				for (int i = 0; i < childs.length; i++) {
					buildEntryNames(childs[i],name,files,entriesBytes);
				}
			}
		}else{
			return;
		}
	}
	
}
