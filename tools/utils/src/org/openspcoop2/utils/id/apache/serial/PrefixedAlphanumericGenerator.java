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
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
 * value is exceeded, an {@link IllegalStateException} is thrown.</p>.
 *
 * Author of the original commons apache code:
 * @author Commons-Id team
 * @version $Id$
 *
 * Authors of the Link.it modification to the code:
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
        StringBuilder sb = new StringBuilder(this.prefix);
        sb.append(super.nextStringIdentifier());
        return sb.toString();
    }
}
