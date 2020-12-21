/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.allarmi.utils.serializer;

import java.io.InputStream;

import org.openspcoop2.utils.serialization.IDeserializer;
import org.openspcoop2.utils.serialization.SerializationConfig;
import org.openspcoop2.utils.serialization.SerializationFactory;
import org.openspcoop2.utils.serialization.SerializationFactory.SERIALIZATION_TYPE;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializerWithFactory extends AbstractDeserializer {

	protected abstract SERIALIZATION_TYPE getSERIALIZATION_TYPE();

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T _xmlToObj(InputStream is, Class<T> c) throws Exception {
		IDeserializer deserializer = SerializationFactory.getDeserializer(this.getSERIALIZATION_TYPE(), new SerializationConfig());
		return (T) deserializer.readObject(is, c);
	}

}
