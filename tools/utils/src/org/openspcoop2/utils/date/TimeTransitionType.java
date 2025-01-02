/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
 * TimeTransitionType
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TimeTransitionType {
	FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME("Standard Time to Daylight Saving Time"),  // Ora solare a ora legale
	FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME("Daylight Saving Time to Standard Time");  // Ora legale a ora solare

    private final String description;

    TimeTransitionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
    
    public String getItaDescription() {
        if(FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME.equals(this)) {
        	return "Da ora solare a ora legale";
        }
        else {
        	return "Da ora legale a ora solare";
        }
    }
}
