/*
 * OpenSPCoop - Customizable API Gateway 
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
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang.ArrayUtils;
import org.openspcoop2.utils.UtilsException;

/**
 * FormatReader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FormatReader {

	/*
	 * Default: Standard comma separated format, as for RFC4180 but allowing empty lines.
	 * Excel: Excel file format (using a comma as the value delimiter).
	 * MySQL: Default MySQL format used by the SELECT INTO OUTFILE and LOAD DATA INFILE operations.
	 * RFC4180: Comma separated format as defined by RFC 4180.
	 * TDF: Tab-delimited format.
	 **/
	public static final String CSV_FORMAT = "format";
	
	/*
	 * Sets the missing column names behavior of the format
	 **/
	public static final String CSV_ALLOW_MISSING_COLUMN_NAMES = "allowMissingColumnNames";
	
	/*
	 * Sets the comment start marker of the format to the specified character.
	 **/
	public static final String CSV_COMMENT_MARKER = "commentMarker";
	
	/*
	 * Sets the delimiter of the format to the specified character.
	 **/
	public static final String CSV_DELIMITER = "delimiter";
	
	/*
	 * Sets the escape character of the format to the specified character.
	 **/
	public static final String CSV_ESCAPE = "escape";
	
	/*
	 * Sets the header of the format.
	 **/
	public static final String CSV_WITH_HEADER = "withHeader";
	public static final String CSV_HEADER = "header";
	
	/*
	 * Sets the empty line skipping behavior of the format to true.
	 **/
	public static final String CSV_WITH_IGNORE_EMPTY_LINES = "ignoreEmptyLines";
	
	/*
	 * Sets the trimming behavior of the format to true.
	 **/
	public static final String CSV_WITH_IGNORE_SURROUNDING_SPACES = "ignoreSurroundingSpaces";
	
	/*
	 * Performs conversions to and from null for strings on input and output.
	 **/
	public static final String CSV_WITH_NULL_STRING = "nullString";
	
	/*
	 * Sets the quoteChar of the format to the specified character.
	 **/
	public static final String CSV_WITH_QUOTE = "quote";
	
	/*
	 * Sets the output quote policy of the format to the specified value.
	 * ALL: Quotes all fields.
	 * MINIMAL: Quotes fields which contain special characters such as a delimiter, quotes character or any of the characters in line separator.
	 * NON_NUMERIC: Quotes all non-numeric fields.
	 * NONE: Never quotes fields.
	 **/
	public static final String CSV_WITH_QUOTE_MODE = "quoteMode";
	
	/*
	 * Sets the record separator of the format to the specified character.
	 **/
	public static final String CSV_WITH_RECORD_SEPARATOR = "recordSeparator";
	
	/*
	 * Sets skipping the header record to true.
	 **/
	public static final String CSV_WITH_SKIP_HEADER_RECORD = "skipHeaderRecord";
		
	/*
	 * Sets skipping the record empty (es. ,,,,,, )
	 **/
	public static final String CSV_SKIP_EMPTY_RECORD = "skipEmptyRecord";
	
	
	
	
	private CSVFormat format;
	private boolean skipEmptyRecord = true;
	
	public FormatReader(InputStream is) throws UtilsException {
		Properties p = null;
		try{
			p = new Properties();
			p.load(is);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		this.init(p);
	}
	public FormatReader(Properties properties) throws UtilsException {
		this.init(properties);
	}
	private void init(Properties properties) throws UtilsException{
		
		// format
		if(properties.containsKey(CSV_FORMAT)){
			String input = properties.getProperty(CSV_FORMAT).trim();
			CSVFormat.Predefined[]p = CSVFormat.Predefined.values();
			boolean found = false;
			for (int i = 0; i < p.length; i++) {
				if(p[i].toString().equalsIgnoreCase(input)){
					this.format = p[i].getFormat();
					found=true;
					break;
				}
			}
			
			if(!found)
				this.format = CSVFormat.DEFAULT;
			
		}
		else{
			this.format = CSVFormat.DEFAULT;
		}
		
		// behaviour
		
		Boolean allowMissingColumnsNames = getBooleanProperty(properties, CSV_ALLOW_MISSING_COLUMN_NAMES);
		if(allowMissingColumnsNames!=null){
			this.format = this.format.withAllowMissingColumnNames(allowMissingColumnsNames);
		}
		
		Character commentMarker = getCharProperty(properties, CSV_COMMENT_MARKER);
		if(commentMarker!=null){
			this.format = this.format.withCommentMarker(commentMarker);
		}
		
		Character delimiter = getCharProperty(properties, CSV_DELIMITER);
		if(delimiter!=null){
			this.format = this.format.withDelimiter(delimiter);
		}
		
		Character escape = getCharProperty(properties, CSV_ESCAPE);
		if(escape!=null){
			this.format = this.format.withEscape(escape);
		}
		
		Boolean withHeader = getBooleanProperty(properties, CSV_WITH_HEADER);
		if(withHeader!=null){
			if(withHeader){
				String [] h = getArrayStringProperty(properties, CSV_HEADER);
				if(h!=null && h.length>0)
					this.format = this.format.withHeader(h);
				else
					this.format = this.format.withHeader();
			}
		}
		else{
			String [] h = getArrayStringProperty(properties, CSV_HEADER);
			if(h!=null && h.length>0){
				this.format = this.format.withHeader(h);
			}
		}
		
		Boolean withIgnoreEmptyLines = getBooleanProperty(properties, CSV_WITH_IGNORE_EMPTY_LINES);
		if(withIgnoreEmptyLines!=null){
			this.format = this.format.withIgnoreEmptyLines(withIgnoreEmptyLines);
		}
		
		Boolean withIgnoreSurroundingSpaces = getBooleanProperty(properties, CSV_WITH_IGNORE_SURROUNDING_SPACES);
		if(withIgnoreSurroundingSpaces!=null){
			this.format = this.format.withIgnoreSurroundingSpaces(withIgnoreSurroundingSpaces);
		}
		
		String withNullString = getProperty(properties, CSV_WITH_NULL_STRING);
		if(withNullString!=null){
			this.format = this.format.withNullString(withNullString);
		}
		
		Character withQuote = getCharProperty(properties, CSV_WITH_QUOTE);
		if(withQuote!=null){
			this.format = this.format.withQuote(withQuote);
		}
		
		String withQuoteMode = getProperty(properties, CSV_WITH_QUOTE_MODE);
		if(withQuoteMode!=null){
			QuoteMode[]q = QuoteMode.values();
			boolean found = false;
			for (int i = 0; i < q.length; i++) {
				if(q[i].toString().equalsIgnoreCase(withQuoteMode)){
					this.format = this.format.withQuoteMode(q[i]);
					found = true;
					break;
				}
			}
			if(!found){
				throw new UtilsException("Quote Mode property ["+CSV_WITH_QUOTE_MODE+"] with wrong value ["+withQuoteMode+"]. Excpected values: "+ArrayUtils.toString(QuoteMode.values()));
			}
		}
		
		Character withRecordSeparator = getCharProperty(properties, CSV_WITH_RECORD_SEPARATOR);
		if(withRecordSeparator!=null){
			this.format = this.format.withRecordSeparator(withRecordSeparator);
		}
		
		Boolean withSkipHeaderRecord = getBooleanProperty(properties, CSV_WITH_SKIP_HEADER_RECORD);
		if(withSkipHeaderRecord!=null){
			this.format = this.format.withSkipHeaderRecord(withSkipHeaderRecord);
		}
		
		Boolean skipEmptyRecord = getBooleanProperty(properties, CSV_SKIP_EMPTY_RECORD);
		if(skipEmptyRecord!=null){
			this.skipEmptyRecord = skipEmptyRecord;
		}
	}
	
	public Format getFormat() {
		Format format = new Format();
		format.setCsvFormat(this.format);
		format.setSkipEmptyRecord(this.skipEmptyRecord);
		return format;
	}
	
	private String[] getArrayStringProperty(Properties properties,String name) throws UtilsException{
		if(properties.containsKey(name)){
			String tmp = properties.getProperty(name);
			if(tmp!=null){
				tmp = tmp.trim();
				String [] ret = tmp.split(",");
				if(ret!=null && ret.length>=1){
					return ret;
				}
				else{
					throw new UtilsException("Valore della proprietà ["+name+"] deve essere una lista di valori separati da ','; trovato invece ["+tmp+"]");
				}
			}
		}
		return null;
	}
	
	private Boolean getBooleanProperty(Properties properties,String name) throws UtilsException{
		if(properties.containsKey(name)){
			String tmp = properties.getProperty(name);
			if(tmp!=null){
				tmp = tmp.trim();
				if("true".equalsIgnoreCase(tmp)){
					return true;
				}
				else if("false".equalsIgnoreCase(tmp)){
					return false;
				}
				else{
					throw new UtilsException("Valore della proprietà ["+name+"] deve essere un valore booleano, trovato invece ["+tmp+"]");
				}
			}
		}
		return null;
	}
	
	private Character getCharProperty(Properties properties,String name) throws UtilsException{
		if(properties.containsKey(name)){
			String tmp = properties.getProperty(name);
			if(tmp!=null){
				tmp = tmp.trim();
				if(tmp.length()==1){
					return tmp.charAt(0);
				}
				else{
					throw new UtilsException("Valore della proprietà ["+name+"] deve essere un singolo carattere, trovato invece ["+tmp+"]");
				}
			}
		}
		return null;
	}
	
	private String getProperty(Properties properties,String name) throws UtilsException{
		if(properties.containsKey(name)){
			String tmp = properties.getProperty(name);
			if(tmp!=null){
				tmp = tmp.trim();
				return tmp;
			}
		}
		return null;
	}


	
}
