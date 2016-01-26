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
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

/**
 * <code>PrefixedNumericGenerator</code> is an Identifier Generator
 * that generates an incrementing number with a prefix as a String object.
 *
 * <p>If the <code>wrap</code> argument passed to the constructor is set to
 * <code>true</code>, the sequence will wrap, returning negative values when
 * {@link Long#MAX_VALUE} reached; otherwise an {@link IllegalStateException}
 * will be thrown.</p>
 *
 * @author Commons-Id team
 * @version $Id$
 */
/**
 * OpenSPCoop2
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PrefixedNumericGenerator extends NumericGenerator {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Prefix. */
    private final String prefix;


    /**
     * Create a new prefixed numeric generator with the specified prefix.
     *
     * @param prefix prefix, must not be null
     * @param wrap should the factory wrap when it reaches the maximum
     *  long value (or throw an exception)
     * @param initialValue the initial long value to start at
     * @throws NullPointerException if prefix is <code>null</code>
     */
    public PrefixedNumericGenerator(String prefix, boolean wrap, long initialValue) {
        super(wrap, initialValue);

        if (prefix == null) {
            throw new NullPointerException("prefix must not be null");
        }
        this.prefix = prefix;
    }


    /**
     * Return the prefix for this prefixed numeric generator.
     *
     * @return the prefix for this prefixed numeric generator
     */
    public String getPrefix() {
        return this.prefix;
    }

    @Override
	public long maxLength() {
        return super.maxLength() + this.prefix.length();
    }

    @Override
	public long minLength() {
        return super.minLength() + this.prefix.length();
    }

    @Override
	public String nextStringIdentifier() throws MaxReachedException {
        StringBuffer sb = new StringBuffer(this.prefix);
        sb.append(super.nextStringIdentifier());
        return sb.toString();
    }
}
