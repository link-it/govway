/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.date;

/**     
 * TimeType
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TimeType {
	STANDARD_TIME("Standard Time"),  // Ora Solare
    DAYLIGHT_SAVING_TIME("Daylight Saving Time");  // Ora Legale

    private final String description;

    TimeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
    
    public String getItaDescription() {
        if(STANDARD_TIME.equals(this)) {
        	return "Ora solare";
        }
        else {
        	return "Ora legale";
        }
    }
}
