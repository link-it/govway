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

/**
 * <code>PrefixedAlphanumericGenerator</code> is an identifier generator
 * that generates an incrementing number in base 36 with a prefix as a String
 * object.
 *
 * <p>All generated ids have the same length (prefixed and padded with 0's
 * on the left), which is determined by the <code>size</code> parameter passed
 * to the constructor.<p>
 *
 * <p>The <code>wrap</code> property determines whether or not the sequence wraps
 * when it reaches the largest value that can be represented in <code>size</code>
 * base 36 digits. If <code>wrap</code> is false and the the maximum representable
 * value is exceeded, an {@link IllegalStateException} is thrown.</p>
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
public class PrefixedAlphanumericGenerator extends AlphanumericGenerator {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Prefix. */
    private final String prefix;


    public PrefixedAlphanumericGenerator(String prefix, boolean wrap) {
        super(wrap, DEFAULT_ALPHANUMERIC_IDENTIFIER_SIZE - ((prefix == null) ? 0 : prefix.length()));

        if (prefix == null) {
            throw new NullPointerException("prefix must not be null");
        }
        if (DEFAULT_ALPHANUMERIC_IDENTIFIER_SIZE <= prefix.length()) {
            throw new IllegalArgumentException("size less prefix length must be at least one");
        }
        this.prefix = prefix;
    }
    public PrefixedAlphanumericGenerator(String prefix, boolean wrap, int size) {
        super(wrap, size - ((prefix == null) ? 0 : prefix.length()));

        if (prefix == null) {
            throw new NullPointerException("prefix must not be null");
        }
        if (size <= prefix.length()) {
            throw new IllegalArgumentException("size less prefix length must be at least one");
        }
        this.prefix = prefix;
    }
    public PrefixedAlphanumericGenerator(String prefix, boolean wrap, String initialValue) {
        super(wrap, (prefix == null) ? initialValue : initialValue.substring(prefix.length()));

        if (prefix == null) {
            throw new NullPointerException("prefix must not be null");
        }
        if (initialValue.length() <= prefix.length()) {
            throw new IllegalArgumentException("size less prefix length must be at least one");
        }
        this.prefix = prefix;
    }


    /**
     * Return the prefix for this prefixed alphanumeric generator.
     *
     * @return the prefix for this prefixed alphanumeric generator
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
	public int getSize() {
        return super.getSize() + this.prefix.length();
    }

    @Override
	public String nextStringIdentifier() throws MaxReachedException {
        StringBuffer sb = new StringBuffer(this.prefix);
        sb.append(super.nextStringIdentifier());
        return sb.toString();
    }
}
