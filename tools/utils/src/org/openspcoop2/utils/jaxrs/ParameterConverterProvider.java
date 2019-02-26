/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

/**	
 * ParameterConverterProvider
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParameterConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> type, Type type1, Annotation[] antns) {
        if (org.joda.time.DateTime.class.equals(type)) {
            @SuppressWarnings("unchecked")
            ParamConverter<T> paramConverter = (ParamConverter<T>) new JodaDateTimeConverter();
            return paramConverter;
        }
        else if (org.joda.time.LocalDate.class.equals(type)) {
            @SuppressWarnings("unchecked")
            ParamConverter<T> paramConverter = (ParamConverter<T>) new JodaLocalDateConverter();
            return paramConverter;
        }
        return null;
    }

}

