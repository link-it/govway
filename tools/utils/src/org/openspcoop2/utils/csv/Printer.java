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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.CharEncoding;
import org.openspcoop2.utils.UtilsException;

/**
 * Printer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Printer {
		
	private OutputStream out;
	private Writer writer;
	private CSVPrinter csvPrinter;
	
	public Printer(Format format, File file) throws UtilsException {
		this(format, file, CharEncoding.UTF_8);
	}
	public Printer(Format format, File file, String charset) throws UtilsException {
		try{
			this.out = new FileOutputStream(file);
			this.writer = new OutputStreamWriter(this.out, charset);
			this.csvPrinter = new CSVPrinter(this.writer, format.getCsvFormat());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public Printer(Format format, OutputStream out) throws UtilsException {
		this(format,out,CharEncoding.UTF_8);
	}
	public Printer(Format format, OutputStream out, String charset) throws UtilsException {
		try{
			this.writer= new OutputStreamWriter(out, charset);
			this.csvPrinter = new CSVPrinter(this.writer, format.getCsvFormat());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public Printer(Format format, Writer writer) throws UtilsException {
		try{
			this.csvPrinter = new CSVPrinter(writer, format.getCsvFormat());
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public Printer(Format format, Appendable appendable) throws UtilsException {
		try{
			this.csvPrinter = new CSVPrinter(appendable, format.getCsvFormat());
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	public void close(){
		try{
			if(this.csvPrinter!=null){
				this.csvPrinter.flush();
				this.csvPrinter.close();
			}
		}catch(Exception eClose){}
		try{
			if(this.writer!=null){
				this.writer.flush();
				this.writer.close();
			}
		}catch(Exception eClose){}
		try{
			if(this.out!=null){
				this.out.flush();
				this.out.close();
			}
		}catch(Exception eClose){}
	}
	
	
	public void println() throws UtilsException{
		try{
			this.csvPrinter.println();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void print(Object line) throws UtilsException{
		try{
			this.csvPrinter.print(line);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void printRecord(Iterable<?> values) throws UtilsException{
		try{
			this.csvPrinter.printRecord(values);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void printRecord(Object ... values) throws UtilsException{
		try{
			this.csvPrinter.printRecord(values);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void printComment(String comment)throws UtilsException{
		try{
			this.csvPrinter.printComment(comment);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
