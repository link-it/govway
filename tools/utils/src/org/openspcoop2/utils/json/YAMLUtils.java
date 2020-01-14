/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**	
 * YAMLUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class YAMLUtils extends AbstractUtils {
	
	private static YAMLUtils yamlUtils = null;
	private static YAMLUtils yamlUtilsPretty = null;
	private static synchronized void init(boolean prettyPrint){
		if(prettyPrint) {
			if(YAMLUtils.yamlUtilsPretty==null){
				YAMLUtils.yamlUtilsPretty = new YAMLUtils(true);
			}
		}
		else {
			if(YAMLUtils.yamlUtils==null){
				YAMLUtils.yamlUtils = new YAMLUtils(false);
			}
		}
	}
	public static YAMLUtils getInstance(){
		return getInstance(false);
	}
	public static YAMLUtils getInstance(boolean prettyPrint){
		if(prettyPrint) {
			if(YAMLUtils.yamlUtilsPretty==null){
				YAMLUtils.init(true);
			}
			return YAMLUtils.yamlUtilsPretty;
		}
		else {
			if(YAMLUtils.yamlUtils==null){
				YAMLUtils.init(false);
			}
			return YAMLUtils.yamlUtils;
		}
	}
	

	private static YAMLMapper mapper;
	private synchronized static void initMapper()  {
		if(mapper==null){
			mapper = new YAMLMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
		}
	}
	public static YAMLMapper getObjectMapper() {
		if(mapper==null){
			initMapper();
		}
		return mapper;
	}
	
	private static ObjectWriter writer;
	private synchronized static void initWriter()  {
		if(mapper==null){
			initMapper();
		}
		if(writer==null){
			writer = mapper.writer();
		}
	}
	public static ObjectWriter getObjectWriter() {
		if(writer==null){
			initWriter();
		}
		return writer;
	}
	
	private static ObjectWriter writerPrettyPrint;
	private synchronized static void initWriterPrettyPrint()  {
		if(mapper==null){
			initMapper();
		}
		if(writerPrettyPrint==null){
			writerPrettyPrint = mapper.writer().withDefaultPrettyPrinter();
		}
	}
	public static ObjectWriter getObjectWriterPrettyPrint() {
		if(writerPrettyPrint==null){
			initWriterPrettyPrint();
		}
		return writerPrettyPrint;
	}
	
	
	
	protected YAMLUtils(boolean prettyPrint) {
		super(prettyPrint);
	}
	
	@Override
	protected void _initMapper() {
		initMapper();
	}
	@Override
	protected void _initWriter(boolean prettyPrint) {
		if(prettyPrint) { 
			initWriterPrettyPrint();
		}
		else {
			initWriter();
		}
	}
	
	@Override
	protected ObjectMapper _getObjectMapper() {
		return getObjectMapper();
	}
	@Override
	protected ObjectWriter _getObjectWriter(boolean prettyPrint) {
		if(prettyPrint) { 
			return getObjectWriterPrettyPrint();
		}
		else {
			return getObjectWriter();
		}
	}
	
	
	// IS
	
	public boolean isYaml(byte[]jsonBytes){
		return !JSONUtils.getInstance().isJson(jsonBytes) && this.isValid(jsonBytes);
	}
	
	public boolean isYaml(String jsonString){
		return !JSONUtils.getInstance().isJson(jsonString) && this.isValid(jsonString);
	}
	
	
}
