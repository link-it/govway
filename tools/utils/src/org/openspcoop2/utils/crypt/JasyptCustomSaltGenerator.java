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
package org.openspcoop2.utils.crypt;

import org.jasypt.salt.SaltGenerator;
import org.openspcoop2.utils.random.RandomGenerator;

/**
 * JasyptCustomSaltGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JasyptCustomSaltGenerator implements SaltGenerator  {
	
	private RandomGenerator randomGenerator;
	private int lastSizeGenerated;
	
	public int getLastSizeGenerated() {
		return this.lastSizeGenerated;
	}

	public JasyptCustomSaltGenerator(RandomGenerator randomGenerator){
		this.randomGenerator = randomGenerator;
	} 
	
	@Override
	public boolean includePlainSaltInEncryptionResults() {
		return true;
	}
	
	@Override
	public byte[] generateSalt(int size) {
		try {
			//System.out.println("GENERA ["+size+"]");
			return this.randomGenerator.nextRandomBytes(size);
		}finally {
			this.lastSizeGenerated = size;
		}
	}
}
