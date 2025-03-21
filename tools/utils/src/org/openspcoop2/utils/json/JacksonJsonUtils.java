/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.util.TimeZone;

import org.openspcoop2.utils.jaxrs.JacksonJsonProviderCustomized;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**	
 * JSONUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JacksonJsonUtils extends AbstractUtils {
	
	
	private static TimeZone timeZone = TimeZone.getDefault();
	public static TimeZone getTimeZone() {
		return timeZone;
	}
	public static void setTimeZone(TimeZone timeZoneParam) {
		timeZone = timeZoneParam;
	}
	public static String getTimeZoneId() {
		return timeZone.getID();
	}
	public static void setTimeZoneById(String timeZoneId) {
		timeZone = TimeZone.getTimeZone(timeZoneId);
	}
	
	private static JacksonJsonUtils jsonUtils = null;
	private static JacksonJsonUtils jsonUtilsPretty = null;
	private static synchronized void init(boolean prettyPrint){
		if(prettyPrint) {
			if(JacksonJsonUtils.jsonUtilsPretty==null){
				JacksonJsonUtils.jsonUtilsPretty = new JacksonJsonUtils(true);
			}
		}
		else {
			if(JacksonJsonUtils.jsonUtils==null){
				JacksonJsonUtils.jsonUtils = new JacksonJsonUtils(false);
			}
		}
	}
	public static JacksonJsonUtils getInstance(){
		return getInstance(false);
	}
	public static JacksonJsonUtils getInstance(boolean prettyPrint){
		if(prettyPrint) {
			if(JacksonJsonUtils.jsonUtilsPretty==null){
				// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED'
				synchronized (JacksonJsonUtils.class) {
					JacksonJsonUtils.init(true);
				}
			}
			return JacksonJsonUtils.jsonUtilsPretty;
		}
		else {
			if(JacksonJsonUtils.jsonUtils==null){
				// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED'
				synchronized (JacksonJsonUtils.class) {
					JacksonJsonUtils.init(false);
				}
			}
			return JacksonJsonUtils.jsonUtils;
		}
	}
	

	private static ObjectMapper mapper;
	private static synchronized void initMapper()  {
		if(mapper==null){
			mapper = JacksonJsonProviderCustomized.getObjectMapper(false, timeZone);
			mapper.setSerializationInclusion(Include.NON_NULL);
		}
	}
	public static ObjectMapper getObjectMapper() {
		if(mapper==null){
			initMapper();
		}
		return mapper;
	}
	
	private static ObjectWriter writer;
	private static synchronized void initWriter()  {
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
	private static synchronized void initWriterPrettyPrint()  {
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
	
	
	
	private JacksonJsonUtils(boolean prettyPrint) {
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
	
	public boolean isJson(byte[]jsonBytes){
		return this.isValid(jsonBytes);
	}
	
	public boolean isJson(String jsonString){
		return this.isValid(jsonString);
	}
	
}
