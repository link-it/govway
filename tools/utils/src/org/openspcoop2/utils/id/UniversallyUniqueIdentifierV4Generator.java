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


package org.openspcoop2.utils.id;

import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
/**
 * Implementazione tramite com.fasterxml.uuid.impl.RandomBasedGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class UniversallyUniqueIdentifierV4Generator extends AbstractUniversallyUniqueIdentifierGenerator{

// Version 4: These are generated from random (or pseudo-random) numbers. If you just need to generate a UUID, this is probably what you want.
//	UUIDv4 from reading RFC4122, it looks like that version does NOT eliminate possibility of collisions. 
//	It is just a random number generator. If that is true, than you have a very GOOD chance of two machines in the world eventually creating the same "UUID"v4 
//	(quotes because there isn't a mechanism for guaranteeing U.niversal U.niqueness). 

	// Generators are fully thread-safe, so a single instance may be shared among multiple threads.
	private RandomBasedGenerator uuidv4 = Generators.randomBasedGenerator();
	
	@Override
	protected UUID generateUUID() {
		return this.uuidv4.generate();
	}

}
