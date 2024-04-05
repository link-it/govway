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

package org.openspcoop2.utils.sql;

/**
 * LikeConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LikeConfig {

	public static LikeConfig contains(boolean caseInsensitive) {
		return contains(true, caseInsensitive);
	}
	public static LikeConfig contains(boolean escape, boolean caseInsensitive) {
		LikeConfig config = new LikeConfig();
		config.setEscape(escape);
		config.setCaseInsensitive(caseInsensitive);
		config.setContains(true);
		return config;
	}
	
	public static LikeConfig startsWith(boolean caseInsensitive) {
		return startsWith(true, caseInsensitive);
	}
	public static LikeConfig startsWith(boolean escape, boolean caseInsensitive) {
		LikeConfig config = new LikeConfig();
		config.setEscape(escape);
		config.setCaseInsensitive(caseInsensitive);
		config.setStartsWith(true);
		return config;
	}
	
	public static LikeConfig endsWith(boolean caseInsensitive) {
		return endsWith(true, caseInsensitive);
	}
	public static LikeConfig endsWith(boolean escape, boolean caseInsensitive) {
		LikeConfig config = new LikeConfig();
		config.setEscape(escape);
		config.setCaseInsensitive(caseInsensitive);
		config.setEndsWith(true);
		return config;
	}
	
	private boolean escape = true;
	
	private boolean contains = false;
	private boolean startsWith = false;
	private boolean endsWith = false;
	
	private boolean caseInsensitive = false;
	
	public boolean isEscape() {
		return this.escape;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public boolean isContains() {
		return this.contains;
	}

	public void setContains(boolean contains) {
		this.contains = contains;
	}

	public boolean isStartsWith() {
		return this.startsWith;
	}

	public void setStartsWith(boolean startsWith) {
		this.startsWith = startsWith;
	}

	public boolean isEndsWith() {
		return this.endsWith;
	}

	public void setEndsWith(boolean endsWith) {
		this.endsWith = endsWith;
	}

	public boolean isCaseInsensitive() {
		return this.caseInsensitive;
	}

	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}
}
