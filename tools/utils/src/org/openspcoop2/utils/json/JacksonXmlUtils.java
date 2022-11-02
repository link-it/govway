/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.jaxrs.JacksonXmlProviderCustomized;

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
public class JacksonXmlUtils extends AbstractUtils {
	
	
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
		
	private static JacksonXmlUtils jsonUtils = null;
	private static JacksonXmlUtils jsonUtilsPretty = null;
	private static synchronized void init(boolean prettyPrint){
		if(prettyPrint) {
			if(JacksonXmlUtils.jsonUtilsPretty==null){
				JacksonXmlUtils.jsonUtilsPretty = new JacksonXmlUtils(true);
			}
		}
		else {
			if(JacksonXmlUtils.jsonUtils==null){
				JacksonXmlUtils.jsonUtils = new JacksonXmlUtils(false);
			}
		}
	}
	public static JacksonXmlUtils getInstance(){
		return getInstance(false);
	}
	public static JacksonXmlUtils getInstance(boolean prettyPrint){
		if(prettyPrint) {
			if(JacksonXmlUtils.jsonUtilsPretty==null){
				JacksonXmlUtils.init(true);
			}
			return JacksonXmlUtils.jsonUtilsPretty;
		}
		else {
			if(JacksonXmlUtils.jsonUtils==null){
				JacksonXmlUtils.init(false);
			}
			return JacksonXmlUtils.jsonUtils;
		}
	}
	

	private static ObjectMapper mapper;
	private synchronized static void initMapper()  {
		if(mapper==null){
			mapper = JacksonXmlProviderCustomized.getObjectMapper(false, timeZone);
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
	
	
	
	protected JacksonXmlUtils(boolean prettyPrint) {
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
