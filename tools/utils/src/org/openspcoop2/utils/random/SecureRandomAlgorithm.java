/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.random;

/**
 * SecureRandomAlgorithm
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum SecureRandomAlgorithm {

	// https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#securerandom-number-generation-algorithms
	
	// The algorithm names in this section can be specified when generating an instance of SecureRandom.

	NATIVE_PRNG("NativePRNG"), // Obtains random numbers from the underlying native OS. No assertions are made as to the blocking nature of generating these numbers.
	NATIVE_PRNG_BLOCKING("NativePRNGBlocking"),	// Obtains random numbers from the underlying native OS, blocking if necessary. For example, /dev/random on UNIX-like systems.
	NATIVE_PRNG_NON_BLOCKING("NativePRNGNonBlocking"), // Obtains random numbers from the underlying native OS, without blocking to prevent applications from excessive stalling. For example, /dev/urandom on UNIX-like systems.
	PKCS11("PKCS11"), // Obtains random numbers from the underlying installed and configured PKCS #11 library.
	DRBG("DRBG"), // An algorithm supplied by the SUN provider using DRBG mechanisms as defined in NIST SP 800-90Ar1.
	SHA1PRNG("SHA1PRNG"), // The name of the pseudo-random number generation (PRNG) algorithm supplied by the SUN provider. This algorithm uses SHA-1 as the foundation of the PRNG. It computes the SHA-1 hash over a true-random seed value concatenated with a 64-bit counter which is incremented by 1 for each operation. From the 160-bit SHA-1 output, only 64 bits are used.
	WINDOWS_PRNG("Windows-PRNG"); // Obtains random numbers from the underlying Windows OS.
	
	SecureRandomAlgorithm(String v){
		this.value = v;
	}
	
	private String value;

	public String getValue() {
		return this.value;
	}
	@Override
	public String toString() {
		return this.value;
	}
}
