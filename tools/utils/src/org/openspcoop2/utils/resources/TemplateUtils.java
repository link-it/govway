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

package org.openspcoop2.utils.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.commons.io.input.CharSequenceReader;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *  TemplateUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TemplateUtils {

	
	/** -------- Utility per la creazione delle configurazioni ------------- */
	
	public static Configuration newTemplateEngine() {
		return TemplateUtils.newTemplateEngine(TemplateUtils.class,"", null,null);
	}
	public static Configuration newTemplateEngine(String prefix) {
		return TemplateUtils.newTemplateEngine(TemplateUtils.class,prefix, null,null);
	}
	public static Configuration newTemplateEngine(Class<?> c) {
		return TemplateUtils.newTemplateEngine(c,"", null,null);
	}
	public static Configuration newTemplateEngine(Class<?> c,String prefix) {
		return TemplateUtils.newTemplateEngine(c, prefix, null,null);
	}
	public static Configuration newTemplateEngine(Class<?> c,String prefix,ObjectWrapper wrapper) {
		return TemplateUtils.newTemplateEngine(c, prefix, wrapper, null);
	}
	public static Configuration newTemplateEngine(Class<?> c,String prefix,ObjectWrapper wrapper,TemplateLoader templateLoader) {
        // Initialize the FreeMarker configuration;
        // - Create a configuration instance
		Configuration cfgFreeMarker = new Configuration(Configuration.VERSION_2_3_23);
		
		cfgFreeMarker.setClassForTemplateLoading(c, prefix);
		
		if(wrapper!=null){
			cfgFreeMarker.setObjectWrapper(wrapper);
		}
		
		if(templateLoader!=null){
			cfgFreeMarker.setTemplateLoader(templateLoader);
		}
		
		return cfgFreeMarker;
    }
	
	
	
	/** -------- Utility per la creazione dei template ------------- */
	
	public static Template getTemplate(Configuration cfg,String templateName) throws IOException{
		return cfg.getTemplate(templateName);
	}
	
	public static Template getTemplate(String templateName) throws IOException{
		Configuration cfg = TemplateUtils.newTemplateEngine();
		return cfg.getTemplate(templateName);
	}
	public static Template getTemplate(String prefix,String templateName) throws IOException{
		Configuration cfg = TemplateUtils.newTemplateEngine(prefix);
		return cfg.getTemplate(templateName);
	}
	public static Template getTemplate(Class<?> c,String prefix,String templateName) throws IOException{
		Configuration cfg = TemplateUtils.newTemplateEngine(c,prefix);
		return cfg.getTemplate(templateName);
	}
	public static Template getTemplate(Class<?> c,String prefix,ObjectWrapper wrapper,String templateName) throws IOException{
		Configuration cfg = TemplateUtils.newTemplateEngine(c,prefix,wrapper);
		return cfg.getTemplate(templateName);
	}
	
	public static Template buildTemplate(String name,byte[] bytes) throws IOException{
		return new Template(name, new CharSequenceReader(new String(bytes)),newTemplateEngine());
	}
	
	
	
	/** -------- Trasformazioni dei template ------------- */
		
	public static byte[] toByteArray(Template template,Map<?, ?> map) throws IOException,TemplateException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(bout);
		template.process(map, writer);
		writer.flush();
		writer.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}
	public static String toString(Template template,Map<?, ?> map) throws IOException,TemplateException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(bout);
		template.process(map, writer);
		writer.flush();
		writer.close();
		bout.flush();
		bout.close();
		return bout.toString();
	}
	public static void writeFile(Template template,Map<?, ?> map,File file,boolean overwrite) throws Exception{
		if(!overwrite){
			if(file.exists()){
				System.out.println(": WARNING !! File ["+file.getAbsolutePath()+"] is already exist, it is not overwritten !!");
				return;
			}
		}
		FileSystemUtilities.mkdirParentDirectory(file);
		FileSystemUtilities.writeFile(file, TemplateUtils.toByteArray(template, map));
	}
	

	
}
