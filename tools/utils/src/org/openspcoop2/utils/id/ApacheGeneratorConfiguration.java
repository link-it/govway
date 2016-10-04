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
package org.openspcoop2.utils.id;

import org.openspcoop2.utils.id.apache.serial.EnumTypeGenerator;

/**
 * ApacheGeneratorConfiguration
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApacheGeneratorConfiguration {

	private EnumTypeGenerator type;
	private boolean wrap;
	private Integer size;
	private Long initalLongValue;
	private String initialStringValue;
	
	private String prefix;
	
	private Character startDigit;
	private Character endDigit;
	private Character startLetter;
	private Character endLetter;
	
	private Boolean enableLowerCaseLetter;
	private Boolean enableUpperCaseLetter;
	
	public EnumTypeGenerator getType() {
		return this.type;
	}
	public void setType(EnumTypeGenerator type) {
		this.type = type;
	}
	public boolean isWrap() {
		return this.wrap;
	}
	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
	public Integer getSize() {
		return this.size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Long getInitalLongValue() {
		return this.initalLongValue;
	}
	public void setInitalLongValue(Long initalLongValue) {
		this.initalLongValue = initalLongValue;
	}
	public String getInitialStringValue() {
		return this.initialStringValue;
	}
	public void setInitialStringValue(String initialStringValue) {
		this.initialStringValue = initialStringValue;
	}
	public Character getStartDigit() {
		return this.startDigit;
	}
	public void setStartDigit(Character startDigit) {
		this.startDigit = startDigit;
	}
	public Character getEndDigit() {
		return this.endDigit;
	}
	public void setEndDigit(Character endDigit) {
		this.endDigit = endDigit;
	}
	public Character getStartLetter() {
		return this.startLetter;
	}
	public void setStartLetter(Character startLetter) {
		this.startLetter = startLetter;
	}
	public Character getEndLetter() {
		return this.endLetter;
	}
	public void setEndLetter(Character endLetter) {
		this.endLetter = endLetter;
	}
	public Boolean isEnableLowerCaseLetter() {
		return this.enableLowerCaseLetter;
	}
	public void setEnableLowerCaseLetter(Boolean enableLowerCaseLetter) {
		this.enableLowerCaseLetter = enableLowerCaseLetter;
	}
	public Boolean isEnableUpperCaseLetter() {
		return this.enableUpperCaseLetter;
	}
	public void setEnableUpperCaseLetter(Boolean enableUpperCaseLetter) {
		this.enableUpperCaseLetter = enableUpperCaseLetter;
	}
	public String getPrefix() {
		return this.prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
