/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk.properties;

import org.openspcoop2.utils.sql.LikeConfig;

/**
 * StringProperty
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StringProperty extends AbstractProperty<String> {

	private LikeConfig searchWithLike; 
	
	protected StringProperty(String id, String value) {
		super(id, value);
	}
	protected StringProperty(String id, String value, LikeConfig searchWithLike) {
		super(id, value);
		this.searchWithLike = searchWithLike;
	}
	
	public LikeConfig getSearchWithLike() {
		return this.searchWithLike;
	}

	public void setSearchWithLike(LikeConfig searchWithLike) {
		this.searchWithLike = searchWithLike;
	}
}
