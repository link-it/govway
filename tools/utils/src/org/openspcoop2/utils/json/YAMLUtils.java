/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
	

	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("JSONUtils");
	private static YAMLMapper _mapper;
	private synchronized static void initMapper()  {
		semaphore.acquireThrowRuntime("initMapper");
		try {
			if(_mapper==null){
				_mapper = new YAMLMapper();
				_mapper.setTimeZone(TimeZone.getDefault());
				_mapper.setSerializationInclusion(Include.NON_NULL);
				_mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
				_mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
					    WRITE_DATES_AS_TIMESTAMPS , false);
				// Since 2.1.4, the field exampleSetFlag appears in json produced with ObjectMapper#writeValue(File, Object) when serializing an object of type OpenApi.
				// "exampleSetFlag" : false
				// Con il codice sottostante, nelle classi Mixin l'attributo 'getExampleSetFlag' viene ignorato
				_mapper.addMixIn(io.swagger.v3.oas.models.media.Schema.class, io.swagger.v3.core.jackson.mixin.SchemaMixin.class);
				_mapper.addMixIn(io.swagger.v3.oas.models.media.MediaType.class, io.swagger.v3.core.jackson.mixin.MediaTypeMixin.class);
			}
		}finally {
			semaphore.release("initMapper");
		}
	}
	public static void setMapperTimeZone(TimeZone timeZone) {
		if(_mapper==null){
			initMapper();
		}
		//synchronized(mapperSynchronized){
		semaphore.acquireThrowRuntime("setMapperTimeZone");
		try {
			_mapper.setTimeZone(timeZone);
		}finally {
			semaphore.release("setMapperTimeZone");
		}
	}
	public static void registerJodaModule() {
		if(_mapper==null){
			initMapper();
		}
		//synchronized(mapperSynchronized){
		semaphore.acquireThrowRuntime("registerJodaModule");
		try {
			_mapper.registerModule(new JodaModule());
		}finally {
			semaphore.release("registerJodaModule");
		}
	}
	public static void registerJavaTimeModule() {
		if(_mapper==null){
			initMapper();
		}
		//synchronized(mapperSynchronized){
		semaphore.acquireThrowRuntime("registerJavaTimeModule");
		try {
			_mapper.registerModule(new JavaTimeModule());
		}finally {
			semaphore.release("registerJavaTimeModule");
		}
	}
	
	public static YAMLMapper getObjectMapper() {
		if(_mapper==null){
			initMapper();
		}
		return _mapper;
	}
	
	private static ObjectWriter writer;
	private synchronized static void initWriter()  {
		if(_mapper==null){
			initMapper();
		}
		if(writer==null){
			writer = _mapper.writer();
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
		if(_mapper==null){
			initMapper();
		}
		if(writerPrettyPrint==null){
			writerPrettyPrint = _mapper.writer().withDefaultPrettyPrinter();
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
