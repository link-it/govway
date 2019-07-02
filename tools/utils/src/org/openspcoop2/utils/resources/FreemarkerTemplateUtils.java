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

package org.openspcoop2.utils.resources;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *  FreemarkerTemplateUtils
 *  
 *  utilizza TemplateUtils per retrocompatibilità
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FreemarkerTemplateUtils {

	
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
       return TemplateUtils.newTemplateEngine(c, prefix, wrapper, templateLoader);
    }
	
	
	
	/** -------- Utility per la creazione dei template ------------- */
	
	public static Template getTemplate(Configuration cfg,String templateName) throws IOException{
		return TemplateUtils.getTemplate(cfg, templateName);
	}
	
	public static Template getTemplate(String templateName) throws IOException{
		return TemplateUtils.getTemplate(templateName);
	}
	public static Template getTemplate(String prefix,String templateName) throws IOException{
		return TemplateUtils.getTemplate(prefix, templateName);
	}
	public static Template getTemplate(Class<?> c,String prefix,String templateName) throws IOException{
		return TemplateUtils.getTemplate(c, prefix, templateName);
	}
	public static Template getTemplate(Class<?> c,String prefix,ObjectWrapper wrapper,String templateName) throws IOException{
		return TemplateUtils.getTemplate(c, prefix, wrapper, templateName);
	}
	
	public static Template buildTemplate(String name,byte[] bytes) throws IOException{
		return TemplateUtils.buildTemplate(name, bytes);
	}
	public static Template buildTemplate(Configuration cfg,String name,byte[] bytes) throws IOException{
		return TemplateUtils.buildTemplate(cfg, name, bytes);
	}
	
	
	
	/** -------- Trasformazioni dei template ------------- */
		
	public static byte[] toByteArray(Template template,Map<?, ?> map) throws IOException,TemplateException{
		return TemplateUtils.toByteArray(template, map);
	}
	public static String toString(Template template,Map<?, ?> map) throws IOException,TemplateException{
		return TemplateUtils.toString(template, map);
	}
	public static void writeFile(Template template,Map<?, ?> map,File file,boolean overwrite) throws Exception{
		TemplateUtils.writeFile(template, map, file, overwrite);
	}
	

	
}
