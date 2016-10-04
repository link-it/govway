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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import org.openspcoop2.utils.id.apache.AbstractStringIdentifierGenerator;


/**
 * <code>TimeBasedAlphanumericIdentifierGenerator</code> is an identifier generator that generates
 * an alphanumeric identifier in base 36 as a String object from the current UTC time and an
 * internal counter.
 * <p>
 * The generator guarantees that all generated ids have an increasing natural sort order (even if
 * the time internally has an overflow). The implementation additionally guarantees, that all
 * instances within the same process do generate unique ids. All generated ids have the same length
 * (padding with 0's on the left), which is determined by the maximum size of a long value and the
 * <code>postfixSize</code> parameter passed to the constructor.
 * </p>
 * <p>
 * Note: To ensure unique ids that are created within the same millisecond (or maximum time
 * resolution of the system), the implementation uses an internal counter. The maximum value of this
 * counter is determined by the <code>postfixSize</code> parameter i.e. the largest value that can
 * be represented in base 36. If the counter exceeds this value, an IllegalStateException is thrown.
 * </p>
 * <p>
 * Note: The uniqueness of the generated ids cannot be guaranteed if the system performs time shifts
 * of more than a second, that affect the running processes.
 * </p>
 * 
 * @author Commons-Id team
 * @version $Id: TimeBasedAlphanumericIdentifierGenerator.java 480488 2006-11-29 08:57:26Z bayard $
 */
/**
 * OpenSPCoop2
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimeBasedAlphanumericIdentifierGenerator extends AbstractStringIdentifierGenerator
        implements Serializable {

    /**
     * <code>serialVersionUID</code> is the serializable UID for the binary version of the class.
     */
    private static final long serialVersionUID = 20060116L;
    /**
     * <code>padding</code> an array of '0' for improved padding performance.
     */
    private static final char[] padding;
    static {
        padding = new char[MAX_LONG_ALPHANUMERIC_VALUE_LENGTH];
        Arrays.fill(padding, '0');
    }
    /**
     * <code>UTC</code> is the UTC {@link TimeZone} instance.
     */
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    /**
     * <code>last</code> is the marker, when the counter was resetted the last time.
     */
    private static long last = 0;
    /**
     * <code>counter</code> counts the number of generated identifiers since the last reset.
     */
    private static long counter = 0;
    /**
     * <code>postfixSize</code> size of the postfix, that contains the padded counter in base 36.
     */
    private final int postfixSize;
    private final long offset;
    
    /**
     * Construct a TimeBasedAlphanumericIdentifierGenerator with a defined size of the postfix and
     * an offset for the time value. The offset can be used to manipulate the representation of the
     * time value in the generated id. If a TimeBasedAlphanumericIdentifierGenerator is constructed
     * with an offset of the current number of milliseconds since 1st Jan 1970 the first generated
     * id in the same millisecond will have an id consisting of a sequence of '0' characters.
     * 
     * @param postfixSize the size of the postfix
     * @param offset the offset taken into account for the time value
     * @throws IllegalArgumentException if <code>postfixSize</code> is negative or exceeds the
     *             maximum size for representing {@link Long#MAX_VALUE} in base 36
     */
    public TimeBasedAlphanumericIdentifierGenerator(final int postfixSize, final long offset) {
        if (postfixSize < 0 || postfixSize > MAX_LONG_ALPHANUMERIC_VALUE_LENGTH) {
            throw new IllegalArgumentException("Invalid size for postfix");
        }
        this.postfixSize = postfixSize;
        this.offset = offset;
    }

    /**
     * Construct a TimeBasedAlphanumericIdentifierGenerator with a defined size of the postfix.
     * 
     * @param postfixSize the size of the postfix defining the maximum number of possible ids
     *            generated within the same millisecond (depending on the time resolution of the
     *            running system)
     * @throws IllegalArgumentException if <code>postfixSize</code> is negative or exceeds the
     *             maximum size for representing {@link Long#MAX_VALUE} in base 36
     */
    public TimeBasedAlphanumericIdentifierGenerator(final int postfixSize) {
        this(postfixSize, 0);
    }

    /**
     * Construct a TimeBasedAlphanumericIdentifierGenerator with a default size of the postfix of 3.
     */
    public TimeBasedAlphanumericIdentifierGenerator() {
        this(3);
    }

    @Override
	public long maxLength() {
        return MAX_LONG_ALPHANUMERIC_VALUE_LENGTH + this.postfixSize;
    }

    @Override
	public long minLength() {
        return maxLength();
    }

    @Override
	public String nextStringIdentifier() throws MaxReachedException {
        long now;
        synchronized (this) {
            now = Calendar.getInstance(UTC).getTime().getTime(); // JDK 1.3 compatibility
            final long diff = now - last;
            // external time correction of more than a second or overflow
            if (diff > 0 || diff < -1000) {
                last = now;
                counter = 0;
            } else {
                if (diff != 0) {
                    now = last; // ignore time shift
                }
                ++counter;
            }
        }
        final String postfix = counter > 0
                                          ? Long.toString(counter, ALPHA_NUMERIC_CHARSET_SIZE) : "";
        if (postfix.length() > this.postfixSize) {
            throw new MaxReachedException(
                    "The maximum number of identifiers in this millisecond has been reached");
        }
        // ensure, that no negative value is used and values stay increasing
        long base = now - this.offset;
        long value = base < 0 ? base + Long.MAX_VALUE + 1 : base;
        final String time = Long.toString(value, ALPHA_NUMERIC_CHARSET_SIZE);
        final char[] buffer = new char[MAX_LONG_ALPHANUMERIC_VALUE_LENGTH + this.postfixSize];
        int i = 0;
        int maxPad = MAX_LONG_ALPHANUMERIC_VALUE_LENGTH - time.length();
        if (maxPad > 0) {
            System.arraycopy(padding, 0, buffer, 0, maxPad);
        }
        System.arraycopy(time.toCharArray(), 0, buffer, maxPad, time.length());
        if (base < 0) {
            // Representation of Long.MAX_VALUE starts with '1', negative 'base' means higher value
            // in time
            buffer[0] += 2;
        }
        i += time.length() + maxPad;
        if (this.postfixSize > 0) {
            maxPad = this.postfixSize - postfix.length();
            if (maxPad > 0) {
                System.arraycopy(padding, 0, buffer, i, maxPad);
                i += maxPad;
            }
            System.arraycopy(postfix.toCharArray(), 0, buffer, i, postfix.length());
        }
        return new String(buffer);
    }

    /**
     * Retrieve the number of milliseconds since 1st Jan 1970 that were the base for the given id.
     * 
     * @param id the id to use
     * @param offset the offset used to create the id
     * @return the number of milliseconds
     * @throws IllegalArgumentException if <code>id</code> is not a valid id from this type of
     *             generator
     */
    public long getMillisecondsFromId(final Object id, final long offset) {
        if (id instanceof String && id.toString().length() >= MAX_LONG_ALPHANUMERIC_VALUE_LENGTH) {
            final char[] buffer = new char[MAX_LONG_ALPHANUMERIC_VALUE_LENGTH];
            System.arraycopy(
                    id.toString().toCharArray(), 0, buffer, 0, MAX_LONG_ALPHANUMERIC_VALUE_LENGTH);
            final boolean overflow = buffer[0] > '1';
            if (overflow) {
                buffer[0] -= 2;
            }
            long value = Long.parseLong(new String(buffer), ALPHA_NUMERIC_CHARSET_SIZE);
            if (overflow) {
                value -= Long.MAX_VALUE + 1;
            }
            return value + offset;
        }
        throw new IllegalArgumentException("'" + id + "' is not an id from this generator");
    }
}
