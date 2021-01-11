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

package org.openspcoop2.protocol.abstraction.csv;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.CharEncoding;
import org.openspcoop2.protocol.abstraction.Erogazione;
import org.openspcoop2.protocol.abstraction.Fruizione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.ManagerUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.csv.Format;
import org.openspcoop2.utils.csv.FormatReader;
import org.openspcoop2.utils.csv.Parser;
import org.openspcoop2.utils.csv.ParserResult;
import org.openspcoop2.utils.csv.Record;
import org.openspcoop2.utils.resources.TemplateUtils;
import org.slf4j.Logger;

import freemarker.template.Template;

/**     
 * Deserializer
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Deserializer {

	private static final String PROTOCOLLO = "protocollo";
	private static final String DEFAULT_SUBJECT_TYPE = "_DefaultSubjectType_";
	
	
	private org.openspcoop2.protocol.abstraction.utils.serializer.JaxbDeserializer jaxbAbstractionDeserializer = null;
	private boolean validate;
	private Logger log;
	public Deserializer(boolean validate,Logger log) throws ProtocolException{
		// -- unmarshall
		this.jaxbAbstractionDeserializer = new org.openspcoop2.protocol.abstraction.utils.serializer.JaxbDeserializer();
		this.validate = validate;
		this.log = log;
	}


	

	
	
	
	
	
	

	// *********** EROGAZIONE ******************

	public List<Erogazione> toErogazione(byte[] erogazioneTemplateParam, byte[] formatParam, byte[] mappingParam, byte[] csv) throws ProtocolException{
		return this.toErogazione(erogazioneTemplateParam, formatParam, mappingParam, csv, CharEncoding.UTF_8);
	}
	public List<Erogazione> toErogazione(byte[] erogazioneTemplateParam, byte[] formatParam, byte[] mappingParam, byte[] csv, String charset) throws ProtocolException{
		Properties format = null;
		if(formatParam!=null){
			try {
				format = Utilities.getAsProperties(formatParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}	
		Properties mapping = null;
		if(mappingParam!=null){
			try {
				mapping = Utilities.getAsProperties(mappingParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}	
		return toErogazione(erogazioneTemplateParam, format, mapping, csv, charset);
	}
	public List<Erogazione> toErogazione(byte[] erogazioneTemplateParam, Properties formatParam, Properties mappingParam, byte[] csvParam) throws ProtocolException{
		return this.toErogazione(erogazioneTemplateParam, formatParam, mappingParam, csvParam, CharEncoding.UTF_8);
	}
	public List<Erogazione> toErogazione(byte[] erogazioneTemplateParam, Properties formatParam, Properties mappingParam, byte[] csvParam, String charset) throws ProtocolException{
		String csv = null;
		if(csvParam==null){
			throw new ProtocolException("CSV non disponibile");
		}
		if(charset==null){
			charset = CharEncoding.UTF_8;
		}
		try{
			csv = new String(csvParam, charset);
		}catch(Exception e){
			throw new ProtocolException("CSV con formato errato [encoding:"+charset+"]: "+e.getMessage());
		}
		return this.toErogazione(erogazioneTemplateParam, formatParam, mappingParam, csv);
	}

	public List<Erogazione> toErogazione(byte[] erogazioneTemplateParam, byte[] formatParam, byte[] mappingParam, String csv) throws ProtocolException{
		Properties format = null;
		if(formatParam!=null){
			try {
				format = Utilities.getAsProperties(formatParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}	
		Properties mapping = null;
		if(mappingParam!=null){
			try {
				mapping = Utilities.getAsProperties(mappingParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		return this.toErogazione(erogazioneTemplateParam, format, mapping, csv);
	}
	public List<Erogazione> toErogazione(byte[] erogazioneTemplateParam, Properties formatParam, Properties mappingParam, String csv) throws ProtocolException{

		try{
		
			byte[] erogazioneTemplate = erogazioneTemplateParam;
			Template template = null;
			if(erogazioneTemplate==null){
				erogazioneTemplate = this.readBytes("/templates/csv/Erogazione.ftl");
			}
			try{
				template = TemplateUtils.buildTemplate("Erogazione", erogazioneTemplate);
			}catch(Exception e){
				throw new ProtocolException("Build Template error: "+e.getMessage());
			}
	
			Properties format = formatParam;
			if(format==null){
				format = this.readProperties("/templates/csv/format.properties");
			}
	
			Properties mapping = mappingParam;
			if(mapping==null){
				mapping = this.readProperties("/templates/csv/erogazione.properties");
			}
	
			List<Erogazione> listErogazioni = new ArrayList<Erogazione>();
	
			// Deserializzo CSV per avere la mappa
			FormatReader formatReader = new FormatReader(format);
			Format f = formatReader.getFormat();
			Parser p = new Parser(mapping, true);
			ParserResult pr = p.parseCsvFile(f, csv);
			for (Record record : pr.getRecords()) {
	
				// Costruisco Mappa
				Map<String, Object> mapFreemarker = new Hashtable<String, Object>();
				if(record.getMap().size()<=0){
					throw new ProtocolException("Csv Record at line ["+record.getCsvLine()+"] not correct, mapping produce 0 fields");
				}
				List<String> keys = record.getMap().keys();
				for (String key : keys) {
					String s = record.getMap().get(key);
					if(s!=null)
						mapFreemarker.put(key, s);
				}
				
				// TipoSoggettoDefault
				String protocollo = record.getMap().get(PROTOCOLLO);
				if(protocollo == null){
					protocollo = ManagerUtils.getDefaultProtocol();
				}
				mapFreemarker.put(DEFAULT_SUBJECT_TYPE, ManagerUtils.getDefaultOrganizationType(protocollo));
				
				// Creo xml della singola erogazione
				byte[] xmlErogazione = null;
				try{
					xmlErogazione = TemplateUtils.toByteArray(template, mapFreemarker);
				}catch(Exception e){
					throw new ProtocolException("Csv Record at line ["+record.getCsvLine()+"] not correct (Build xml from template failed): "+e.getMessage());
				}
				ByteArrayInputStream bin = null;
				try{
					if(this.validate){
						bin = new ByteArrayInputStream(xmlErogazione);
						org.openspcoop2.protocol.abstraction.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
					}
					Erogazione erogazione = this.jaxbAbstractionDeserializer.readErogazione(xmlErogazione);
					listErogazioni.add(erogazione);
				}catch(Exception e){
					throw new ProtocolException("Csv Record at line ["+record.getCsvLine()+"] not correct. Marshall Xml ["+new String(xmlErogazione)+"]\n"+e.getMessage(),e);
				}
				finally{
					try{
						if(bin!=null)
							bin.close();
					}catch(Exception e){}
				}
			}
	
			return listErogazioni;
			
		} catch (UtilsException e) {
			throw new ProtocolException(e.getMessage(),e);
		}

	}






	// *********** FRUIZIONE ******************

	public List<Fruizione> toFruizione(byte[] fruizioneTemplateParam, byte[] formatParam, byte[] mappingParam, byte[] csv) throws ProtocolException{
		return this.toFruizione(fruizioneTemplateParam, formatParam, mappingParam, csv, CharEncoding.UTF_8);
	}
	public List<Fruizione> toFruizione(byte[] fruizioneTemplateParam, byte[] formatParam, byte[] mappingParam, byte[] csv, String charset) throws ProtocolException{
		Properties format = null;
		if(formatParam!=null){
			try{
				format = Utilities.getAsProperties(formatParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}	
		Properties mapping = null;
		if(mappingParam!=null){
			try{
				mapping = Utilities.getAsProperties(mappingParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}	
		return toFruizione(fruizioneTemplateParam, format, mapping, csv, charset);
	}
	public List<Fruizione> toFruizione(byte[] fruizioneTemplateParam, Properties formatParam, Properties mappingParam, byte[] csvParam) throws ProtocolException{
		return this.toFruizione(fruizioneTemplateParam, formatParam, mappingParam, csvParam, CharEncoding.UTF_8);
	}
	public List<Fruizione> toFruizione(byte[] fruizioneTemplateParam, Properties formatParam, Properties mappingParam, byte[] csvParam, String charset) throws ProtocolException{
		String csv = null;
		if(csvParam==null){
			throw new ProtocolException("CSV non disponibile");
		}
		if(charset==null){
			charset = CharEncoding.UTF_8;
		}
		try{
			csv = new String(csvParam, charset);
		}catch(Exception e){
			throw new ProtocolException("CSV con formato errato [encoding:"+charset+"]: "+e.getMessage());
		}
		return this.toFruizione(fruizioneTemplateParam, formatParam, mappingParam, csv);
	}

	public List<Fruizione> toFruizione(byte[] fruizioneTemplateParam, byte[] formatParam, byte[] mappingParam, String csv) throws ProtocolException{
		Properties format = null;
		if(formatParam!=null){
			try{
				format = Utilities.getAsProperties(formatParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}	
		Properties mapping = null;
		if(mappingParam!=null){
			try{
				mapping = Utilities.getAsProperties(mappingParam);
			} catch (UtilsException e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		return this.toFruizione(fruizioneTemplateParam, format, mapping, csv);
	}
	public List<Fruizione> toFruizione(byte[] fruizioneTemplateParam, Properties formatParam, Properties mappingParam, String csv) throws ProtocolException{

		try{
		
			byte[] fruizioneTemplate = fruizioneTemplateParam;
			Template template = null;
			if(fruizioneTemplate==null){
				fruizioneTemplate = this.readBytes("/templates/csv/Fruizione.ftl");
			}
			try{
				template = TemplateUtils.buildTemplate("Fruizione", fruizioneTemplate);
			}catch(Exception e){
				throw new ProtocolException("Build Template error: "+e.getMessage());
			}
	
			Properties format = formatParam;
			if(format==null){
				format = this.readProperties("/templates/csv/format.properties");
			}
	
			Properties mapping = mappingParam;
			if(mapping==null){
				mapping = this.readProperties("/templates/csv/fruizione.properties");
			}
	
			List<Fruizione> listFruizioni = new ArrayList<Fruizione>();
	
			// Deserializzo CSV per avere la mappa
			FormatReader formatReader = new FormatReader(format);
			Format f = formatReader.getFormat();
			Parser p = new Parser(mapping, true);
			ParserResult pr = p.parseCsvFile(f, csv);
			for (Record record : pr.getRecords()) {
	
				// Costruisco Mappa
				Map<String, Object> mapFreemarker = new Hashtable<String, Object>();
				if(record.getMap().size()<=0){
					throw new ProtocolException("Csv Record at line ["+record.getCsvLine()+"] not correct, mapping produce 0 fields");
				}
				List<String> keys = record.getMap().keys();
				for (String key : keys) {
					String s = record.getMap().get(key);
					if(s!=null)
						mapFreemarker.put(key, s);
				}
				
				// TipoSoggettoDefault
				String protocollo = record.getMap().get(PROTOCOLLO);
				if(protocollo == null){
					protocollo = ManagerUtils.getDefaultProtocol();
				}
				mapFreemarker.put(DEFAULT_SUBJECT_TYPE, ManagerUtils.getDefaultOrganizationType(protocollo));
				
				// Creo xml della singola fruizione
				byte[] xmlFruizione = null;
				try{
					xmlFruizione = TemplateUtils.toByteArray(template, mapFreemarker);
				}catch(Exception e){
					throw new ProtocolException("Csv Record at line ["+record.getCsvLine()+"] not correct (Build xml from template failed): "+e.getMessage());
				}
				ByteArrayInputStream bin = null;
				try{
					if(this.validate){
						bin = new ByteArrayInputStream(xmlFruizione);
						org.openspcoop2.protocol.abstraction.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
					}
					Fruizione fruizione = this.jaxbAbstractionDeserializer.readFruizione(xmlFruizione);
					listFruizioni.add(fruizione);
				}catch(Exception e){
					throw new ProtocolException("Csv Record at line ["+record.getCsvLine()+"] not correct. Marshall Xml ["+new String(xmlFruizione)+"]\n"+e.getMessage(),e);
				}
				finally{
					try{
						if(bin!=null)
							bin.close();
					}catch(Exception e){}
				}
			}
	
			return listFruizioni;
			
		} catch (UtilsException e) {
			throw new ProtocolException(e.getMessage(),e);
		}

	}









	// *********** UTILS ******************

	private byte[] readBytes(String resourceName) throws UtilsException{
		InputStream is = null;
		try{
			is = Deserializer.class.getResourceAsStream(resourceName);
			if(is==null){
				throw new UtilsException("["+resourceName+"] non disponibile");
			}
			return Utilities.getAsByteArray(is);
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}	
	}

	private Properties readProperties(String resourceName) throws UtilsException{
		InputStream isProp = null;
		try{
			isProp = Deserializer.class.getResourceAsStream(resourceName);
			if(isProp==null){
				throw new UtilsException("Format non disponibile");
			}
			Properties p = new Properties();
			p.load(isProp);
			return p;
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				if(isProp!=null){
					isProp.close();
				}
			}catch(Exception eClose){}
		}
	}

}
