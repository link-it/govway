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
 * Modificato da Link.it (https://link.it) per supportare le seguenti funzionalità:
 * - Generazione ID all'interno delle interfacce di OpenSPCoop2
 * - Gestione caratteri massimi per numeri e cifre
 * - Possibilità di utilizzare lowerCase e/o upperCase
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
 */
package org.openspcoop2.utils.id.apache;

import org.openspcoop2.utils.id.apache.serial.MaxReachedException;

/**
 * Abstract superclass for LongIdentifierGenerator implementations.
 *
 * Author of the original commons apache code:
 * @author Commons-Id team
 * @version $Id$
 *
 * Authors of the Link.it modification to the code:
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
