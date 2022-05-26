/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.io.IOException;
import java.io.InputStream;

import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

/**
 * IDUnivocoGroupByPolicyStreamSerializer 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDUnivocoGroupByPolicyStreamSerializer  implements StreamSerializer<IDUnivocoGroupByPolicy> {

	@Override
	public int getTypeId() {
		return 1;
	}

	@Override
	public IDUnivocoGroupByPolicy read(ObjectDataInput inParam) throws IOException {
		InputStream inputStream = (InputStream) inParam;
		try {
			String s = Utilities.getAsString(inputStream, Charset.UTF_8.getValue());
			return IDUnivocoGroupByPolicy.deserialize(s);
		}catch(Exception e) {
			throw new IOException(e.getMessage(),e);
		}
		
	}

	@Override
	public void write(ObjectDataOutput out, IDUnivocoGroupByPolicy id) throws IOException {
		out.write(IDUnivocoGroupByPolicy.serialize(id).getBytes(Charset.UTF_8.getValue()));
	}

}
