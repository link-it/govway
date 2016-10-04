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

package org.openspcoop2.utils.id.apache.serial;

import org.openspcoop2.utils.id.apache.AbstractStringIdentifierGenerator;

import java.io.Serializable;

/**
 * <p><code>NumericIdentifierGenerator</code> is an Identifier Generator
 * that generates an incrementing number as a String object.</p>
 *
 * <p>If the <code>wrap</code> argument passed to the constructor is set to
 * <code>true</code>, the sequence will wrap, returning negative values when
 * {@link Long#MAX_VALUE} reached; otherwise an {@link IllegalStateException}
 * will be thrown.</p>
 *
 * @author Commons-Id team
 * @version $Id: NumericGenerator.java 480488 2006-11-29 08:57:26Z bayard $
 */
/**
 * OpenSPCoop2
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NumericGenerator extends AbstractStringIdentifierGenerator implements Serializable {

    /**
     * <code>serialVersionUID</code> is the serializable UID for the binary version of the class.
     */
    private static final long serialVersionUID = 20060121L;
    
    /** Should the counter wrap. */
    private boolean wrapping;
    /** The counter. */
    private long count = 0;

    /**
     * <p>Constructor.</p>
     *
     * @param wrap should the factory wrap when it reaches the maximum
     *  long value (or throw an exception)
     * @param initialValue  the initial long value to start at
     */
    public NumericGenerator(boolean wrap, long initialValue) {
        super();
        this.wrapping = wrap;
        this.count = initialValue;
    }

    /**
     * Returns the maximum length (number or characters) for an identifier
     * from this sequence.
     * 
     * <p>The maximum value is determined from the length of the string
     * representation of {@link Long#MAX_VALUE}.</p>
     *
     * @return the maximum identifier length
     */
    @Override
	public long maxLength() {
        return AbstractStringIdentifierGenerator.MAX_LONG_NUMERIC_VALUE_LENGTH;
    }

    /**
     * <p>Returns the minimum length (number of characters) for an identifier
     * from this sequence.</p>
     *
     * @return the minimum identifier length: <code>1</code>
     */
    @Override
	public long minLength() {
        return 1;
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
	public String nextStringIdentifier() throws MaxReachedException {
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
        return Long.toString(value);
    }
}
