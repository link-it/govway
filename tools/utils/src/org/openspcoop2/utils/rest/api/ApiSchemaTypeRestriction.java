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

package org.openspcoop2.utils.rest.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.BaseBean;

/**
 * ApiSchemaTypeRestriction
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiSchemaTypeRestriction extends BaseBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object schema;
	
	private String format;
	private String type;
	
	
	// ** NUMBER **
	
	// minimum ≤ value ≤ maximum
	private BigDecimal minimum;
	private BigDecimal maximum;
	/*
	 * exclusiveMinimum: false or not included	value ≥ minimum
	 * exclusiveMinimum: true	value > minimum
	 * exclusiveMaximum: false or not included	value ≤ maximum
	 * exclusiveMaximum: true	value < maximum
	 */
	private Boolean exclusiveMinimum;
	private Boolean exclusiveMaximum; 
	
	// The example 10 above matches 10, 20, 30, 0, -10, -20, and so on. 
	// The value of multipleOf must be a positive number, that is, you cannot use multipleOf: -5.
	private BigDecimal multipleOf;
		

	// ** STRING **
	
	private Long minLength;
	private Long maxLength;
	
	/*
	 * Note that the regular expression is enclosed in the ^…$ tokens, where ^ means the beginning of the string, and $ means the end of the string. 
	 * Without ^…$, pattern works as a partial match, that is, matches any string that contains the specified regular expression. 
	 * For example, pattern: pet matches pet, petstore and carpet. The ^…$ token forces an exact match.
	 **/
	private String pattern;
	
	
	// ** ENUM **
	
	private List<?> enumValues;
	
	
	private final static String TYPE = "type";
	private final static String FORMAT = "format";
	private final static String MINIMUM = "minimum";
	private final static String EXCLUSIVE_MINIMUM = "exclusiveMinimum";
	private final static String MAXIMUM = "maximum";
	private final static String EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
	private final static String MULTIPLE_OF = "multipleOf";
	private final static String MIN_LENGTH = "minLength";
	private final static String MAX_LENGTH = "maxLength";
	private final static String PATTERN = "pattern";
	private final static String ENUM_VALUES = "enumValues";
	
	public static ApiSchemaTypeRestriction toApiSchemaTypeRestriction(String restriction) throws UtilsException {
		if(restriction!=null) {
			restriction = restriction.trim();	
		}
		if(restriction==null || "".equals(restriction) || !restriction.contains("=")) {
			throw new UtilsException("Formato non valido");
		}
		ApiSchemaTypeRestriction schema = new ApiSchemaTypeRestriction();
		
		String [] tmp = restriction.split(";");
		if(tmp==null || tmp.length<=0) {
			throw new UtilsException("Formato non valido");
		}
		for (int i = 0; i < tmp.length; i++) {
			
			String regola = tmp[i];
			if(regola!=null) {
				regola = regola.trim();
			}
			if(regola==null || "".equals(regola) || !regola.contains("=")) {
				throw new UtilsException("Formato non valido (trovata regola '"+regola+"' senza '=')");
			}
			
			String [] nomeValore = regola.split("=");
			if(nomeValore==null || nomeValore.length!=2) {
				throw new UtilsException("Formato non valido (regola '"+regola+"'); atteso tipo=restrizione");
			}
			
			String tipo = nomeValore[0];
			if(tipo!=null) {
				tipo = tipo.trim();	
			}
			if(tipo==null || "".equals(tipo)) {
				throw new UtilsException("Formato non valido (regola '"+regola+"'); atteso tipo=restrizione");
			}
			
			String restrizione = nomeValore[1];
			if(restrizione!=null) {
				restrizione = restrizione.trim();	
			}
			if(restrizione==null || "".equals(restrizione)) {
				throw new UtilsException("Formato non valido (regola '"+regola+"'); atteso tipo=restrizione");
			}
			
			try {
				if(TYPE.equalsIgnoreCase(tipo)) {
					schema.setType(restrizione);
				}
				else if(FORMAT.equalsIgnoreCase(tipo)) {
					schema.setFormat(restrizione);
				}
				else if(MINIMUM.equalsIgnoreCase(tipo)) {
					schema.setMinimum(new BigDecimal(restrizione));
				}
				else if(EXCLUSIVE_MINIMUM.equalsIgnoreCase(tipo)) {
					schema.setExclusiveMinimum(Boolean.parseBoolean(restrizione));
				}
				else if(MAXIMUM.equalsIgnoreCase(tipo)) {
					schema.setMaximum(new BigDecimal(restrizione));
				}
				else if(EXCLUSIVE_MAXIMUM.equalsIgnoreCase(tipo)) {
					schema.setExclusiveMaximum(Boolean.parseBoolean(restrizione));
				}
				else if(MULTIPLE_OF.equalsIgnoreCase(tipo)) {
					schema.setMultipleOf(new BigDecimal(restrizione));
				}
				else if(MIN_LENGTH.equalsIgnoreCase(tipo)) {
					schema.setMinLength(Long.valueOf(restrizione));
				}
				else if(MAX_LENGTH.equalsIgnoreCase(tipo)) {
					schema.setMaxLength(Long.valueOf(restrizione));
				}
				else if(PATTERN.equalsIgnoreCase(tipo)) {
					schema.setPattern(restrizione);
				}
				else if(ENUM_VALUES.equalsIgnoreCase(tipo)) {
					String [] values = restrizione.split(",");
					if(values!=null && values.length>0) {
						List<String> l = new ArrayList<>();
						for (String v : values) {
							if(v!=null) {
								v = v.trim();
								l.add(v);
							}
						}
						if(!l.isEmpty()) {
							schema.setEnumValues(l);
						}
					}
				}
				else {
					throw new UtilsException("Trovata regola '"+regola+"' con un tipo '"+tipo+"' sconosciuto");
				}
			}
			catch(UtilsException u) {
				throw u;
			}
			catch(Exception e) {
				throw new UtilsException("Trovata regola '"+regola+"' con una restrizione non valida: "+e.getMessage());
			}
			
		}
		
		return schema;
	}
	public static String toString(ApiSchemaTypeRestriction restriction) throws UtilsException {
		return restriction.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if(this.type!=null) {
			sb.append(TYPE).append("=").append(this.type);
		}
		if(this.format!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(FORMAT).append("=").append(this.format);
		}
		
		if(this.minimum!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(MINIMUM).append("=").append(this.minimum);
		}
		if(this.exclusiveMinimum!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(EXCLUSIVE_MINIMUM).append("=").append(this.exclusiveMinimum);
		}
		if(this.maximum!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(MAXIMUM).append("=").append(this.maximum);
		}
		if(this.exclusiveMaximum!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(EXCLUSIVE_MAXIMUM).append("=").append(this.exclusiveMaximum);
		}
		
		if(this.multipleOf!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(MULTIPLE_OF).append("=").append(this.multipleOf);
		}
		
		if(this.minLength!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(MIN_LENGTH).append("=").append(this.minLength);
		}
		if(this.maxLength!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(MAX_LENGTH).append("=").append(this.maxLength);
		}
		
		if(this.pattern!=null) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			sb.append(PATTERN).append("=").append(this.pattern);
		}
		
		if(this.enumValues!=null && !this.enumValues.isEmpty()) {
			if(sb.length()>0) {
				sb.append("; ");
			}
			StringBuilder sbList = new StringBuilder();
			for (Object object : this.enumValues) {
				if(sbList.length()>0) {
					sbList.append(",");
				}
				sbList.append(object);
			}
			sb.append(ENUM_VALUES).append("=").append(sbList.toString());
		}
		
		return sb.toString();
	}
	
	
	
	public Object getSchema() {
		return this.schema;
	}

	public void setSchema(Object schema) {
		this.schema = schema;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getMinimum() {
		return this.minimum;
	}

	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
	}

	public BigDecimal getMaximum() {
		return this.maximum;
	}

	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
	}

	public Boolean getExclusiveMinimum() {
		return this.exclusiveMinimum;
	}

	public void setExclusiveMinimum(Boolean exclusiveMinimum) {
		this.exclusiveMinimum = exclusiveMinimum;
	}

	public Boolean getExclusiveMaximum() {
		return this.exclusiveMaximum;
	}

	public void setExclusiveMaximum(Boolean exclusiveMaximum) {
		this.exclusiveMaximum = exclusiveMaximum;
	}

	public BigDecimal getMultipleOf() {
		return this.multipleOf;
	}

	public void setMultipleOf(BigDecimal multipleOf) {
		this.multipleOf = multipleOf;
	}

	public Long getMinLength() {
		return this.minLength;
	}

	public void setMinLength(Long minLength) {
		this.minLength = minLength;
	}

	public Long getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(Long maxLength) {
		this.maxLength = maxLength;
	}

	public String getPattern() {
		return this.pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public List<?> getEnumValues() {
		return this.enumValues;
	}

	public void setEnumValues(List<?> enumValues) {
		this.enumValues = enumValues;
	}
}
