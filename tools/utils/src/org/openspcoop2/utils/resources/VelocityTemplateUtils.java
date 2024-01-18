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

package org.openspcoop2.utils.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;



/**
 *  TemplateUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VelocityTemplateUtils {

	
	/** -------- Utility per la creazione delle configurazioni ------------- */
	
	public static VelocityEngine newTemplateEngine() {
		VelocityEngine engine = new VelocityEngine();
		engine.init();
		return engine;
    }
	public static VelocityEngine newTemplateEngine(String propsFileName) {
		VelocityEngine engine = new VelocityEngine();
		engine.init(propsFileName);
		return engine;
    }
	public static VelocityEngine newTemplateEngine(Properties p) {
		VelocityEngine engine = new VelocityEngine();
		engine.init(p);
		return engine;
    }
	
	
	
	/** -------- Utility per la creazione dei template ------------- */
	
	public static Template getTemplate(VelocityEngine engine,String templateName) throws IOException{
		return engine.getTemplate(templateName);
	}
	public static Template getTemplate(VelocityEngine engine,String templateName, String encoding) throws IOException{
		return engine.getTemplate(templateName, encoding);
	}
	
	public static Template buildTemplate(String name,byte[] bytes) throws IOException, ParseException{
		return buildTemplate(name, bytes, Charset.UTF_8.getValue(), null);
	}
	public static Template buildTemplate(String name,byte[] bytes, String encoding) throws IOException, ParseException{
		return buildTemplate(name, bytes, encoding,
				null);
	}
	
	public static Template buildTemplate(String name,byte[] bytes,
			Map<String, byte[]> templateIncludes) throws IOException, ParseException{
		return buildTemplate(name, bytes, Charset.UTF_8.getValue(), 
				templateIncludes);
	}
	public static Template buildTemplate(String name,byte[] bytes, String encoding,
			Map<String, byte[]> templateIncludes) throws IOException, ParseException{
		
		RuntimeInstance instance = new RuntimeInstance();
		
		String defaultTemplate = "_____DEFAULT_TEMPLATE___";
		
		if(templateIncludes==null) {
			templateIncludes= new HashMap<String, byte[]>();
		}
		VelocityTemplateLoader loader = new VelocityTemplateLoader(templateIncludes);
		templateIncludes.put(defaultTemplate, bytes);
		instance.setProperty(RuntimeConstants.RESOURCE_LOADER, "govway_rl" );
		instance.setProperty("govway_rl."+RuntimeConstants.RESOURCE_LOADER+"."+RuntimeConstants.RESOURCE_LOADER_INSTANCE, loader);

        instance.init();

        Template template = instance.getTemplate(defaultTemplate, encoding);
        
		/*
		Metodo che non consentiva la risoluzione dei template interni
		Template template = new Template();
		template.setName(name);
		template.setEncoding(encoding);
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(new String(bytes));
		SimpleNode node = runtimeServices.parse(reader, template);
		template.setRuntimeServices(runtimeServices);
		template.setData(node);
		template.initDocument();
		*/
		return template;
	}
	
	
	
	/** -------- Trasformazioni dei template ------------- */
		
	public static VelocityContext toVelocityContext(Map<String, Object> map) {
		VelocityContext context = new VelocityContext(map);
		return context;
	}
	
	public static byte[] toByteArray(Template template,Map<String, Object> map) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(bout);
		template.merge( toVelocityContext(map), writer );
		writer.flush();
		writer.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}
	public static String toString(Template template,Map<String, Object> map) throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(bout);
		template.merge( toVelocityContext(map), writer );
		writer.flush();
		writer.close();
		bout.flush();
		bout.close();
		return bout.toString();
	}
	public static void writeFile(Template template,Map<String, Object> map,File file,boolean overwrite) throws Exception{
		if(!overwrite){
			if(file.exists()){
				System.out.println(": WARNING !! File ["+file.getAbsolutePath()+"] is already exist, it is not overwritten !!");
				return;
			}
		}
		FileSystemUtilities.mkdirParentDirectory(file);
		FileSystemUtilities.writeFile(file, VelocityTemplateUtils.toByteArray(template, map));
	}
	

	
}
