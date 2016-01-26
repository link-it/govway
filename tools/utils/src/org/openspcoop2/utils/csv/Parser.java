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

package org.openspcoop2.utils.csv;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * Parser
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Parser {

	private static final String PARSER_NAME_PREFIX_= "mapping.field.";
	private static final String PARSER_REGEXP_PREFIX_= "mapping.regexp.";
	private static final String PARSER_REGEXP_SUFFIX_NOT_FOUND= ".notFound";
	private static final String PARSER_DEFAULT_PREFIX_= "mapping.default.";
	private static final String PARSER_CONSTANT_PREFIX_= "mapping.constant.";
	private static final String PARSER_REQUIRED_PREFIX_= "mapping.required.";
	
	private List<ParserMappingRecord> mapping;
	
	public Parser(List<ParserMappingRecord> mapping) throws UtilsException {
		if(mapping==null || mapping.size()<=0){
			throw new UtilsException("Map is null");
		}
		for (int i = 0; i < mapping.size(); i++) {
			if(mapping.get(i).getName()==null){
				throw new UtilsException("RecordMapping["+i+"] without name");
			}
			if(mapping.get(i).getConstantValue()==null && mapping.get(i).getCsvPosition()==null && mapping.get(i).getCsvColumnName()==null ){
				throw new UtilsException("RecordMapping["+i+"] name["+mapping.get(i).getName()+"] without almost one required field: constantValue, csvPosition, csvColumnName");
			}
		}
		this.mapping = mapping;
	}
	public Parser(InputStream is,boolean positionMapping) throws UtilsException {
		Properties p = null;
		try{
			p = new Properties();
			p.load(is);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		this.init(p,positionMapping);
	}
	public Parser(Properties properties,boolean positionMapping) throws UtilsException {
		this.init(properties,positionMapping);
	}
	
	private void init(Properties properties,boolean positionMapping) throws UtilsException{
		
		Properties names = Utilities.readProperties(PARSER_NAME_PREFIX_, properties);
		Properties regexps = Utilities.readProperties(PARSER_REGEXP_PREFIX_, properties);
		Properties constants = Utilities.readProperties(PARSER_CONSTANT_PREFIX_, properties);
		Properties required = Utilities.readProperties(PARSER_REQUIRED_PREFIX_, properties);
		Properties defaults = Utilities.readProperties(PARSER_DEFAULT_PREFIX_, properties);
		
		if(names.size()<=0){
			throw new UtilsException("No mapping exists");
		}
		
		this.mapping = new ArrayList<ParserMappingRecord>();
		
		
		Enumeration<?> enNames = names.keys();
		while (enNames.hasMoreElements()) {
			String key = (String) enNames.nextElement();
			String value = names.getProperty(key);
			if(value==null){
				throw new UtilsException("Property ["+PARSER_NAME_PREFIX_+key+"] without value");
			}
			value = value.trim();
			
			ParserMappingRecord recordMapping = null;
						
			if(constants.containsKey(key)){
				if("true".equalsIgnoreCase(constants.getProperty(key).trim())){
					// 	E' una costante
					recordMapping = ParserMappingRecord.newCsvConstantRecord(key, value);
				}
				else if("false".equalsIgnoreCase(required.getProperty(key).trim())){
					// verrà gestita come campo normale.
				}
				else{
					throw new UtilsException("Property ["+PARSER_CONSTANT_PREFIX_+key+"] with wrong value (expected true/false): "+required.getProperty(key).trim());
				}
			}
			
			if(recordMapping==null){
				// non è una costante
			
				String [] regExpr = null;
				ParserRegexpNotFound regexpNotFoundBehaviour = null;
				if(regexps.containsKey(key)){
					if(regexps.getProperty(key)!=null){
						List<String> r = new ArrayList<String>();
						r.add(regexps.getProperty(key).trim());
						int index = 1;
						while(regexps.containsKey(key+"."+index) && regexps.getProperty(key+"."+index)!=null){
							r.add(regexps.getProperty(key+"."+index).trim());
							index++;
						}
						regExpr = r.toArray(new String[1]);
						
						if(regexps.containsKey(key+PARSER_REGEXP_SUFFIX_NOT_FOUND)){
							if(regexps.getProperty(key+PARSER_REGEXP_SUFFIX_NOT_FOUND)!=null){
								String tmp = regexps.getProperty(key+PARSER_REGEXP_SUFFIX_NOT_FOUND).trim();
								ParserRegexpNotFound [] p = ParserRegexpNotFound.values();
								for (int i = 0; i < p.length; i++) {
									if(p[i].toString().equals(tmp)){
										regexpNotFoundBehaviour = p[i];
										break;
									}
								}
								if(regexpNotFoundBehaviour==null){
									throw new UtilsException("Property ["+PARSER_REGEXP_PREFIX_+key+PARSER_REGEXP_SUFFIX_NOT_FOUND+"] with wrong value (expected: "+ArrayUtils.toString(p)+"): "+tmp);
								}
							}
						}
					}
				}
				
				String defaultValue = null;
				if(defaults.containsKey(key)){
					if(defaults.getProperty(key)!=null){
						defaultValue = defaults.getProperty(key).trim();
					}
				}
				
				if(positionMapping){
					try{
						int intValue = Integer.parseInt(value);
						if(intValue<0){
							throw new Exception("Negative Number");
						}
						recordMapping = ParserMappingRecord.newCsvColumnPositionRecord(key, intValue, defaultValue, regexpNotFoundBehaviour, regExpr);
					}catch(Exception e){
						throw new UtilsException("Property ["+PARSER_NAME_PREFIX_+key+"] with wrong value (expected positive number): "+e.getMessage(),e);
					}
				}
				else{
					recordMapping = ParserMappingRecord.newCsvColumnNameRecord(key, value, defaultValue, regexpNotFoundBehaviour, regExpr);
				}
				
				if(required.containsKey(key)){
					if("true".equalsIgnoreCase(required.getProperty(key).trim())){
						recordMapping.setRequired(true);
					}
					else if("false".equalsIgnoreCase(required.getProperty(key).trim())){
						recordMapping.setRequired(false);
					}
					else{
						throw new UtilsException("Property ["+PARSER_REQUIRED_PREFIX_+key+"] with wrong value (expected true/false): "+required.getProperty(key).trim());
					}
				}
				
			}
			
			this.mapping.add(recordMapping);
		}
		
	}
	
	public ParserResult parseCsvFile(Format format, String csvContent) throws UtilsException{
		Reader reader = null;
		try{
			reader = new StringReader(csvContent);
			return this.parseCsvFile(format, reader);
		}finally{
			try{
				if(reader!=null){
					reader.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public ParserResult parseCsvFile(Format format, byte[] csv) throws UtilsException{
		return this.parseCsvFile(format, csv, CharEncoding.UTF_8,false);
	}
	public ParserResult parseCsvFile(Format format, byte[] csv, String charset) throws UtilsException{
		return this.parseCsvFile(format, csv, charset,false);
	}
	public ParserResult parseCsvFile(Format format, byte[] csv,boolean enableBomInputStream) throws UtilsException{
		return this.parseCsvFile(format, csv, CharEncoding.UTF_8,enableBomInputStream);
	}
	public ParserResult parseCsvFile(Format format, byte[] csv, String charset,boolean enableBomInputStream) throws UtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(csv);
			return this.parseCsvFile(format, bin,charset,enableBomInputStream);
		}finally{
			try{
				if(bin!=null){
					bin.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public ParserResult parseCsvFile(Format format, File file) throws UtilsException{
		return this.parseCsvFile(format, file, CharEncoding.UTF_8, false);
	}
	public ParserResult parseCsvFile(Format format, File file, String charset) throws UtilsException{
		return this.parseCsvFile(format, file, charset, false);
	}
	public ParserResult parseCsvFile(Format format, File file, boolean enableBomInputStream) throws UtilsException{
		return this.parseCsvFile(format, file, CharEncoding.UTF_8, enableBomInputStream);
	}
	public ParserResult parseCsvFile(Format format, File file, String charset,boolean enableBomInputStream) throws UtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return this.parseCsvFile(format, fin,charset,enableBomInputStream);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(fin!=null){
					fin.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public ParserResult parseCsvFile(Format format, InputStream is) throws UtilsException{
		return parseCsvFile(format, is, CharEncoding.UTF_8, false);
	}
	public ParserResult parseCsvFile(Format format, InputStream is, String charset) throws UtilsException{
		return parseCsvFile(format, is, charset, false);
	}
	public ParserResult parseCsvFile(Format format, InputStream is, boolean enableBomInputStream) throws UtilsException{
		return parseCsvFile(format, is, CharEncoding.UTF_8, enableBomInputStream);
	}
	public ParserResult parseCsvFile(Format format, InputStream is, String charset,boolean enableBomInputStream) throws UtilsException{
		Reader reader = null;
		BOMInputStream bomInputStream = null;
		try{
			if(enableBomInputStream){
				bomInputStream = new BOMInputStream(is);
				reader = new InputStreamReader(bomInputStream,charset);
			}
			else{
				reader = new InputStreamReader(is,charset);
			}
			return this.parseCsvFile(format, reader);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(bomInputStream!=null){
					bomInputStream.close();
				}
			}catch(Exception eClose){}
			try{
				if(reader!=null){
					reader.close();
				}
			}catch(Exception eClose){}
		}
	}
	public ParserResult parseCsvFile(Format format, Reader reader) throws UtilsException{
		
		CSVParser parser = null;
		ParserResult parserResult = null;
		try{
			parser = new CSVParser(reader, format.getCsvFormat());
			
			parserResult = new ParserResult();
			parserResult.setHeaderMap(parser.getHeaderMap());
			
			for(CSVRecord record : parser.getRecords()){
				if(format.isSkipEmptyRecord()){
					if(isRecordEmpty(record)){
						continue;
					}
				}
				
				Record recordBean = new Record();
				recordBean.setComment(record.getComment());
				recordBean.setCsvLine(record.getRecordNumber());
				recordBean.setRecord(record);
				MapResult recordMap = new MapResult();
				
				try{
					for (int i = 0; i < this.mapping.size(); i++) {
						
						ParserMappingRecord mappingRecord = this.mapping.get(i);
						String key = mappingRecord.getName();
						String valore = null;
						
						if(mappingRecord.getConstantValue()!=null){
							valore = mappingRecord.getConstantValue();
						}
						else{
							String tmpValue = null;
							if(mappingRecord.getCsvPosition()!=null){
								if(mappingRecord.getCsvPosition()<record.size()){
									tmpValue = record.get(mappingRecord.getCsvPosition());
								}
								else{
									throw new Exception("Record with index ["+mappingRecord.getCsvPosition()+"] is greather or equals record size ["+record.size()+"]");
								}
							}
							else{
								if(record.isMapped(mappingRecord.getCsvColumnName())){
									tmpValue = record.get(mappingRecord.getCsvColumnName());
								}
								else{
									throw new Exception("Record with column name ["+mappingRecord.getCsvColumnName()+"] not exists");
								}
							}
							if(tmpValue!=null && mappingRecord.getRegexpr()!=null){
								String [] pattern = mappingRecord.getRegexpr();
								String regExpValue = null;
								for (int j = 0; j < pattern.length; j++) {
									try{
										regExpValue = RegularExpressionEngine.getStringMatchPattern(tmpValue, pattern[j]);
										if(regExpValue!=null){
											break;
										}
									}catch(RegExpNotFoundException notFound){}	
								}
								if(regExpValue==null){
									if(ParserRegexpNotFound.ERROR.equals(mappingRecord.getRegexpNotFoundBehaviour())){
										throw new Exception("Mapping for field ["+mappingRecord.getName()+"] failed, regular expression not match (value ["+tmpValue+"])");
									}
									else if(ParserRegexpNotFound.NULL.equals(mappingRecord.getRegexpNotFoundBehaviour())){
										tmpValue = null;
									}
									else if(ParserRegexpNotFound.ORIGINAL.equals(mappingRecord.getRegexpNotFoundBehaviour())){
										//tmpValue = tmpValue;
									}
								}
								else{
									tmpValue = regExpValue;
								}
							}
							if(tmpValue==null && mappingRecord.getDefaultValue()!=null){
								tmpValue = mappingRecord.getDefaultValue();
							}
							if(tmpValue==null && mappingRecord.isRequired()){
								throw new Exception("Mapping for field ["+mappingRecord.getName()+"] failed");
							}
							valore = tmpValue;
						}
						
						recordMap.add(key, valore);
					}
					
		
				}catch(Exception e){
					throw new Exception("Record Line["+record.getRecordNumber()+"] ["+record+"]: "+e.getMessage(),e);
				}
				finally{
					try{
						if(parser!=null){
							parser.close();
							parser = null;
						}
					}catch(Exception eClose){}
				}
				
				recordBean.setMap(recordMap);
				parserResult.getRecords().add(recordBean);
			}
			
			return parserResult;
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				if(parser!=null){
					parser.close();
				}
			}catch(Exception eClose){}
		}
		
	}
	
	
	public static boolean isRecordEmpty(CSVRecord record){
		for (int i = 0; i < record.size(); i++) {
			if(record.get(i)!=null && !record.get(i).trim().equals("") ){
				return false;
			}
		}
		return true;
	}

}
