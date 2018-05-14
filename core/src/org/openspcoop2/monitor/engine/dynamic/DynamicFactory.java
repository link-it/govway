package org.openspcoop2.monitor.engine.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import org.openspcoop2.monitor.sdk.exceptions.SearchException;

/**
 * DynamicFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicFactory {

	private static DynamicFactory basicLoaderFactory;
	public static synchronized void initialize(File repositoryJars) throws SearchException{
		if(basicLoaderFactory==null){
			basicLoaderFactory = new DynamicFactory(repositoryJars);
		}
	}
	public static DynamicFactory getInstance() throws SearchException{
		if(basicLoaderFactory==null){
			throw new SearchException("Not Initialized");
		}
		return basicLoaderFactory;
	}
	
	
	private File repositoryJars;
	private DynamicClassLoader jarClassLoader;
	private DynamicFactory(File repositoryJars) throws SearchException{
		
		if(repositoryJars!=null){
			if(repositoryJars.exists()==false){
				throw new SearchException("Repository ["+repositoryJars.getAbsolutePath()+"] not exists");
			}
			if(repositoryJars.isDirectory()==false){
				throw new SearchException("Repository ["+repositoryJars.getAbsolutePath()+"] not directory");
			}
			if(repositoryJars.canRead()==false){
				throw new SearchException("Repository ["+repositoryJars.getAbsolutePath()+"] not readable");
			}
			
			this.repositoryJars = repositoryJars;
			this.updateJars();
		}
	}
	
	public void updateJars() throws SearchException{
		try{
			if(this.repositoryJars!=null){
				List<URL> listURL = new ArrayList<URL>();
				File [] f = this.repositoryJars.listFiles();
				if(f!=null && f.length>0){
					for (int i = 0; i < f.length; i++) {
						if(f[i].getName().endsWith(".jar")){
							//URL jarfile = new URL("jar", "","file:" + f[i].getAbsolutePath()+"!/");    
							listURL.add(f[i].toURI().toURL());
							//listURL.add(jarfile);
							//System.out.println("ADD ["+jarfile+"]");
						}
					}
					this.jarClassLoader = new DynamicClassLoader( listURL.toArray(new URL[1]) );   
				}
			}
		}catch(Exception e){
			if(this.repositoryJars!=null)
				throw new SearchException("Update jars in repository ["+this.repositoryJars.getAbsolutePath()+"] error: "+e.getMessage(),e);
			else
				throw new SearchException(e.getMessage(),e);
		}
	}
	
	public IDynamicLoader newDynamicLoader(String className,Logger log) throws SearchException{
		Class<?> c = null;
		ByteArrayOutputStream bout = null;
		PrintWriter writer = null;
		try{
			bout = new ByteArrayOutputStream();
			writer = new PrintWriter(bout);
			try{
				if(this.jarClassLoader!=null){
					c = this.jarClassLoader.loadClass(className);
				}
			}catch(Throwable e){
				writer.println("Lookup with jarClassLoader("+this.repositoryJars.getAbsolutePath()+") failed: ");
				e.printStackTrace(writer);
				writer.println("\n\n");
			}
			if(c==null){
				try{
					// classLoader normale
					c = Class.forName(className);
				}catch(Throwable e){
					writer.println("Lookup with default classLoader failed: ");
					e.printStackTrace(writer);
				}
			}
			if(c==null){
				try{
					writer.flush();
					bout.flush();
				}catch(Exception eClose){}
				//throw new SearchException(bout.toString());
				log.error(bout.toString());
				throw new SearchException("Impossibile caricare il plugin. La classe indicata ["+className+"] non esiste.");
			}
			return new BasicLoader(className, c);
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(Exception eClose){}
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	
	public IDynamicFilter newDynamicFilter(String className,Logger log) throws SearchException{
		return new BasicFilter(className);
	}
	
	public IDynamicValidator newDynamicValidator(String className,Logger log) throws SearchException{
		return new BasicValidator(className);
	}
}
