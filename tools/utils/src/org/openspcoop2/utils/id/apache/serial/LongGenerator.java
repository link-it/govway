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
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
 */

package org.openspcoop2.utils.id.apache.serial;

import org.openspcoop2.utils.id.apache.AbstractLongIdentifierGenerator;

import java.io.Serializable;

/**
 * <code>LongGenerator</code> is an Identifier Generator
 * that generates an incrementing number as a Long object.
 *
 * <p>If the <code>wrap</code> argument passed to the constructor is set to
 * <code>true</code>, the sequence will wrap, returning negative values when
 * {@link Long#MAX_VALUE} reached; otherwise an {@link IllegalStateException}
 * will be thrown.</p>.
 *
 * Author of the original commons apache code:
 * @author Commons-Id team
 * @version $Id$
 *
 * Authors of the Link.it modification to the code:
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LongGenerator extends AbstractLongIdentifierGenerator implements Serializable {

    /**
     * <code>serialVersionUID</code> is the serializable UID for the binary version of the class.
     */
    private static final long serialVersionUID = 20060122L;

    /** Should the counter wrap. */
    private boolean wrapping;
    /** The counter. */
    private long count = 0;
    
    /**
     * Constructor.
     *
     * @param wrap should the factory wrap when it reaches the maximum
     *  long value (or throw an exception)
     * @param initialValue  the initial long value to start at
     */
    public LongGenerator(boolean wrap, long initialValue) {
        super();
        this.wrapping = wrap;
        this.count = initialValue;
    }

    /**
     * Getter for property wrap.
     *
     * @return <code>true</code> if this generator is set up to wrap.
     *
     */
    public boolean isWrap() {
        return this.wrapping;
    }

    /**
     * Sets the wrap property.
     *
     * @param wrap value for the wrap property
     *
     */
    public void setWrap(boolean wrap) {
        this.wrapping = wrap;
    }
    
    @Override
	public Long nextLongIdentifier() throws MaxReachedException {
        long value = 0;
        if (this.wrapping) {
            synchronized (this) {
                value = this.count++;
            }
        } else {
            synchronized (this) {
                if (this.count == Long.MAX_VALUE) {
                    throw new MaxReachedException
                    ("The maximum number of identifiers has been reached");
                }
                value = this.count++;
            }
        }
        return value;
    }
}
