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

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

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
	
	static {
		YamlSnakeLimits.initialize();
	}
	
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
	private static YAMLMapper internalMapper;
	private static synchronized void initMapper()  {
		semaphore.acquireThrowRuntime("initMapper");
		try {
			if(internalMapper==null){
				internalMapper = new YAMLMapper();
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
	
	public static YAMLMapper getObjectMapper() {
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
	
	
	// UTILS per ANCHOR
	
	public static boolean containsMergeKeyAnchor(String yaml) {
		return yaml!=null && yaml.contains("<<: *");
	}
	
	public static String resolveMergeKeyAndConvertToJson(String yaml) throws UtilsException {
		return resolveMergeKeyAndConvertToJson(yaml, JSONUtils.getInstance());
	}
	public static String resolveMergeKeyAndConvertToJson(String yaml, JSONUtils jsonUtils) throws UtilsException {
		// Fix merge key '<<: *'
		// La funzionalità di merge key è supportata fino allo yaml 1.1 (https://ktomk.github.io/writing/yaml-anchor-alias-and-merge-key.html)
		// Mentre OpenAPI dice di usare preferibilmente YAML 1.2 (https://swagger.io/specification/):
		//   "n order to preserve the ability to round-trip between YAML and JSON formats, YAML version 1.2 is RECOMMENDED"
		// Inoltre le anchor utilizzate nelle merge key non sono supportate correttamente in jackson:
		//   https://github.com/FasterXML/jackson-dataformats-text/issues/98
		// Mentre vengono gestite correttamente da snake (https://linuxtut.com/convert-json-and-yaml-in-java-(using-jackson-and-snakeyaml)-0ad0a/)
		// Come fix quindi nel caso siano presenti viene fatta una serializzazione tramite snake che le risolve.
		if(containsMergeKeyAnchor(yaml)) {
			// Risoluzione merge key '<<: *'
			Map<String, Object> obj = new org.yaml.snakeyaml.Yaml().load(yaml);
			return jsonUtils.toString(obj); // jsonRepresentation
		}
		return null;
	}
	
	
	// CONVERT TO MAP 
	
	public Map<String, Serializable> convertToMap(Logger log, String source, String raw) {
		return this.convertToMap(log, source, raw, null);
	}
	public Map<String, Serializable> convertToMap(Logger log, String source, String raw, List<String> claimsToConvert) {
		if(this.isYaml(raw)) {
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
		if(this.isYaml(raw)) {
			return super.convertToMapEngine(log, source, raw, claimsToConvert);
		}
		else {
			return new HashMap<>(); // empty return
		}	
	}
}
