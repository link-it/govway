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

package org.openspcoop2.utils.json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**	
 * JSONUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JSONUtils extends AbstractUtils {
	
	private static JSONUtils jsonUtils = null;
	private static JSONUtils jsonUtilsPretty = null;
	private static synchronized void init(boolean prettyPrint){
		if(prettyPrint) {
			if(JSONUtils.jsonUtilsPretty==null){
				JSONUtils.jsonUtilsPretty = new JSONUtils(true);
			}
		}
		else {
			if(JSONUtils.jsonUtils==null){
				JSONUtils.jsonUtils = new JSONUtils(false);
			}
		}
	}
	public static JSONUtils getInstance(){
		return getInstance(false);
	}
	public static JSONUtils getInstance(boolean prettyPrint){
		if(prettyPrint) {
			if(JSONUtils.jsonUtilsPretty==null){
				JSONUtils.init(true);
			}
			return JSONUtils.jsonUtilsPretty;
		}
		else {
			if(JSONUtils.jsonUtils==null){
				JSONUtils.init(false);
			}
			return JSONUtils.jsonUtils;
		}
	}
	

	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("JSONUtils");
	private static ObjectMapper internalMapper;
	private static synchronized void initSyncMapper()  {
		if(internalMapper==null){
			internalMapper = new ObjectMapper();
			internalMapper.setTimeZone(TimeZone.getDefault());
			internalMapper.setSerializationInclusion(Include.NON_NULL);
			internalMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
			internalMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
				    WRITE_DATES_AS_TIMESTAMPS , false);
			// Since 2.1.4, the field exampleSetFlag appears in json produced with ObjectMapper#writeValue(File, Object) when serializing an object of type OpenApi.
			// "exampleSetFlag" : false
			// Con il codice sottostante, nelle classi Mixin l'attributo 'getExampleSetFlag' viene ignorato
			internalMapper.addMixIn(io.swagger.v3.oas.models.media.Schema.class, io.swagger.v3.core.jackson.mixin.SchemaMixin.class);
			internalMapper.addMixIn(io.swagger.v3.oas.models.media.MediaType.class, io.swagger.v3.core.jackson.mixin.MediaTypeMixin.class);
		}
	}
	private static void initMapper()  {
		semaphore.acquireThrowRuntime("initMapper");
		try {
			if(internalMapper==null){
				initSyncMapper();
			}
		}finally {
			semaphore.release("initMapper");
		}
	}
	public static void setMapperTimeZone(TimeZone timeZone) {
		if(internalMapper==null){
			initMapper();
		}
		semaphore.acquireThrowRuntime("setMapperTimeZone");
		try {
			internalMapper.setTimeZone(timeZone);
		}finally {
			semaphore.release("setMapperTimeZone");
		}
	}
	public static void registerJodaModule() {
		if(internalMapper==null){
			initMapper();
		}
		semaphore.acquireThrowRuntime("registerJodaModule");
		try {
			internalMapper.registerModule(new JodaModule());
		}finally {
			semaphore.release("registerJodaModule");
		}
	}
	public static void registerJavaTimeModule() {
		if(internalMapper==null){
			initMapper();
		}
		semaphore.acquireThrowRuntime("registerJavaTimeModule");
		try {
			internalMapper.registerModule(new JavaTimeModule());
		}finally {
			semaphore.release("registerJavaTimeModule");
		}
	}
	
	public static ObjectMapper getObjectMapper() {
		if(internalMapper==null){
			initMapper();
		}
		return internalMapper;
	}
	
	private static ObjectWriter writer;
	private static synchronized void initWriter()  {
		if(internalMapper==null){
			initMapper();
		}
		if(writer==null){
			writer = internalMapper.writer();
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
		if(internalMapper==null){
			initMapper();
		}
		if(writerPrettyPrint==null){
			writerPrettyPrint = internalMapper.writer().withDefaultPrettyPrinter();
		}
	}
	public static ObjectWriter getObjectWriterPrettyPrint() {
		if(writerPrettyPrint==null){
			initWriterPrettyPrint();
		}
		return writerPrettyPrint;
	}
	
	
	
	protected JSONUtils(boolean prettyPrint) {
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
	
	
	// CONVERT TO MAP 
	
	public Map<String, Serializable> convertToMap(Logger log, String source, String raw) {
		return this.convertToMap(log, source, raw, null);
	}
	public Map<String, Serializable> convertToMap(Logger log, String source, String raw, List<String> claimsToConvert) {
		if(this.isJson(raw)) {
			return super.convertToMapEngine(log, source, raw, claimsToConvert);
		}
		else {
			return new HashMap<>(); // empty return
		}	
	}
	
	public Map<String, Serializable> convertToMap(Logger log, String source, byte[]raw) {
		return this.convertToMap(log, source, raw, null);
	}
	public Map<String, Serializable> convertToMap(Logger log, String source, byte[]raw, List<String> claimsToConvert) {
		if(this.isJson(raw)) {
			return super.convertToMapEngine(log, source, raw, claimsToConvert);
		}
		else {
			return new HashMap<>(); // empty return
		}	
	}
	
}
