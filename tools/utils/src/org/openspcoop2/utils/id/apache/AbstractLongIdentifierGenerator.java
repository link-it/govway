/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Modificato per supportare le seguenti funzionalita':
 * - Generazione ID all'interno delle interfacce di OpenSPCoop2
 * 
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
package org.openspcoop2.utils.id.apache;

import org.openspcoop2.utils.id.apache.serial.MaxReachedException;

/**
 * Abstract superclass for LongIdentifierGenerator implementations.
 *
 * @author Commons-Id team
 * @version $Id: AbstractLongIdentifierGenerator.java 480488 2006-11-29 08:57:26Z bayard $
 */
/**
 * OpenSPCoop2
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractLongIdentifierGenerator implements LongIdentifierGenerator {

    /**
     * Constructor.
     */
    protected AbstractLongIdentifierGenerator() {
        super();
    }

    /**
     * Returns the maximum value of an identifier from this generator.
     *
     * <p>The default implementation returns Long.MAX_VALUE. Implementations
     * whose identifiers are bounded below Long.MAX_VALUE should override this method to
     * return the maximum value of a generated identifier.</p>
     *
     * @return {@inheritDoc}
     */
    @Override
	public long maxValue() {
        return Long.MAX_VALUE;
    }

    /**
     * Returns the minimum value of an identifier from this generator.
     *
     * <p>The default implementation returns Long.MIN_VALUE. Implementations
     * whose identifiers are bounded above Long.MIN_VALUE should override this method to
     * return the minimum value of a generated identifier.</p>
     *
     * @return {@inheritDoc}
     */
    @Override
	public long minValue() {
        return Long.MIN_VALUE;
    }

    @Override
	public Object nextIdentifier() throws MaxReachedException {
        return this.nextLongIdentifier();
    }

    @Override
	public abstract Long nextLongIdentifier() throws MaxReachedException;
}
