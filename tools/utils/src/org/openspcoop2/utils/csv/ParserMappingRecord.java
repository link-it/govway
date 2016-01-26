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

/**
 * ParserMappingRecord
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParserMappingRecord {

	public static ParserMappingRecord newCsvColumnPositionRecord(String name,Integer csvPosition){
		return newCsvColumnPositionRecord(name, csvPosition, null, null, (String[]) null);
	}
	public static ParserMappingRecord newCsvColumnPositionRecord(String name,Integer csvPosition,String defaultValue){
		return newCsvColumnPositionRecord(name, csvPosition, defaultValue, null, (String[]) null);
	}
	public static ParserMappingRecord newCsvColumnPositionRecord(String name,Integer csvPosition,String ... regexpr){
		return newCsvColumnPositionRecord(name, csvPosition, null, null, regexpr);
	}
	public static ParserMappingRecord newCsvColumnPositionRecord(String name,Integer csvPosition,ParserRegexpNotFound regexpNotFoundBehaviour,String ... regexpr){
		return newCsvColumnPositionRecord(name, csvPosition, null, regexpNotFoundBehaviour, regexpr);
	}
	public static ParserMappingRecord newCsvColumnPositionRecord(String name,Integer csvPosition,String defaultValue,String ... regexpr){
		return newCsvColumnPositionRecord(name, csvPosition, defaultValue, null, regexpr);
	}
	public static ParserMappingRecord newCsvColumnPositionRecord(String name,Integer csvPosition,String defaultValue,ParserRegexpNotFound regexpNotFoundBehaviour,String ... regexpr){
		ParserMappingRecord pmr = new ParserMappingRecord();
		pmr.name=name;
		pmr.csvPosition=csvPosition;
		pmr.defaultValue = defaultValue;
		pmr.regexpr=regexpr;
		if(regexpNotFoundBehaviour!=null)
			pmr.regexpNotFoundBehaviour=regexpNotFoundBehaviour;
		return pmr;
	}
	
	
	public static ParserMappingRecord newCsvColumnNameRecord(String name,String csvColumnName){
		return newCsvColumnNameRecord(name, csvColumnName, null, null, (String[]) null);
	}
	public static ParserMappingRecord newCsvColumnNameRecord(String name,String csvColumnName,String defaultValue){
		return newCsvColumnNameRecord(name, csvColumnName, defaultValue, null, (String[]) null);
	}
	public static ParserMappingRecord newCsvColumnNameRecord(String name,String csvColumnName,String ... regexpr){
		return newCsvColumnNameRecord(name, csvColumnName, null, null, regexpr);
	}
	public static ParserMappingRecord newCsvColumnNameRecord(String name,String csvColumnName,ParserRegexpNotFound regexpNotFoundBehaviour,String ... regexpr){
		return newCsvColumnNameRecord(name, csvColumnName, null, regexpNotFoundBehaviour, regexpr);
	}
	public static ParserMappingRecord newCsvColumnNameRecord(String name,String csvColumnName,String defaultValue,String ... regexpr){
		return newCsvColumnNameRecord(name, csvColumnName, defaultValue, null, regexpr);
	}
	public static ParserMappingRecord newCsvColumnNameRecord(String name,String csvColumnName,String defaultValue,ParserRegexpNotFound regexpNotFoundBehaviour,String ... regexpr){
		ParserMappingRecord pmr = new ParserMappingRecord();
		pmr.name=name;
		pmr.csvColumnName=csvColumnName;
		pmr.defaultValue = defaultValue;
		pmr.regexpr=regexpr;
		if(regexpNotFoundBehaviour!=null)
			pmr.regexpNotFoundBehaviour=regexpNotFoundBehaviour;
		return pmr;
	}
	
	
	public static ParserMappingRecord newCsvConstantRecord(String name,String constantValue){
		ParserMappingRecord pmr = new ParserMappingRecord();
		pmr.name=name;
		pmr.constantValue=constantValue;
		return pmr;
	}
	
	private ParserMappingRecord(){}
	
	
	private boolean required = false;
	private String name;
	private Integer csvPosition;
	private String csvColumnName;
	private String [] regexpr;
	private ParserRegexpNotFound regexpNotFoundBehaviour = ParserRegexpNotFound.NULL;
	private String constantValue;
	private String defaultValue;
	
	public boolean isRequired() {
		return this.required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getName() {
		return this.name;
	}
	public Integer getCsvPosition() {
		return this.csvPosition;
	}
	public String getCsvColumnName() {
		return this.csvColumnName;
	}
	public String [] getRegexpr() {
		return this.regexpr;
	}
	public String getConstantValue() {
		return this.constantValue;
	}
	public ParserRegexpNotFound getRegexpNotFoundBehaviour() {
		return this.regexpNotFoundBehaviour;
	}
	public String getDefaultValue() {
		return this.defaultValue;
	}
}
