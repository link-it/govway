/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.digest.test;

import java.nio.ByteBuffer;
import java.util.Base64;

import org.bouncycastle.util.Arrays;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.digest.DigestConfig;
import org.openspcoop2.utils.digest.DigestFactory;
import org.openspcoop2.utils.digest.DigestType;
import org.openspcoop2.utils.digest.IDigest;
import org.slf4j.LoggerFactory;

/**
 * DigestTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DigestTest {
	
	private static final Long SEED_1 = 5195720880L;
	private static final Integer SEED_2 = 51988113;
	
	private static final String TEXT_1 = "M3ssaggi0 S3Gr3T0!";
	private static final String TEXT_2 = "T35T0 Ch1ar()";
	
	private static final String HASH_COMPOSITION_1 = "${message}${salt}";
	private static final String HASH_COMPOSITION_2 = "-${message}-${salt}-";
	
	// dati generati usando https://emn178.github.io/online-tools/
	private static final Object[][] TEST_DATA = {
			{DigestType.SHA256, HASH_COMPOSITION_2, SEED_1, TEXT_1, "JNwGBJupODTVWx0UStmfUejrsYUX4mnfQL0zQifBVXU="},
			{DigestType.SHA256, HASH_COMPOSITION_2, SEED_2, TEXT_1, "lF6AEXat0Xc25LEWlpLYl+pZ/SaW9j1jrHEP+HoyYHg="},
			{DigestType.SHA256, HASH_COMPOSITION_2, SEED_1, TEXT_2, "Who7CBEy7heN5ipw3LtTcxXDqDkpJk2G5FlIzLPlELg="},
			
			{DigestType.SHA256, HASH_COMPOSITION_1, SEED_1, TEXT_1, "USan20lu/WtPhUmLgxAaj8SPAy1u89CRuiqpi7BBbOk="},
			{DigestType.SHA256, HASH_COMPOSITION_1, SEED_2, TEXT_1, "JeCsH6nJVbZ5PPc4ETgLN4uiCKdFFHTyY2yvx1H0QRs="},
			{DigestType.SHA256, HASH_COMPOSITION_1, SEED_1, TEXT_2, "XwYQiEq84zq1AI+1ov/xYASieHXS1i5xOoE3BzDq9Fc="},
			
			{DigestType.SHA384, HASH_COMPOSITION_1, SEED_1, TEXT_1, "CW+DM/NAKvRLDGD67lbIOV9PokJViIJ5bkwG31bNRPJ6/FQXi1Jai7yVJ8zzHX6F"},
			{DigestType.SHA384, HASH_COMPOSITION_1, SEED_2, TEXT_1, "bE8I/jYx4qjJDw3VdeaN9rNQOs8e545FMZfY9aS2iol7EBsaRzNiP/jRkA2vDI+B"},
			{DigestType.SHA384, HASH_COMPOSITION_1, SEED_1, TEXT_2, "2Wu/IGj6R6u9eWqfCXu3TXHy91gemNJWA751H+nNzieHxMjp5WJ6IDlJhzh7XXln"},
			
			{DigestType.SHA512, HASH_COMPOSITION_1, SEED_1, TEXT_1, "xeTxFp6KDMD6wK1/vKTUV5+qdoweUXlcw4eY/gXLX0PBeQJrLSQavvEA/gVzrbhhk2Yjak/A6Be+HfkIoPmVxg=="},
			{DigestType.SHA512, HASH_COMPOSITION_1, SEED_2, TEXT_1, "e4UCUSVfBPaYgDfVUUNQ9wvin9ruyuwNDPgyvdK5C0T9152fluhUWpAut3xpRotTNm5DqjSRp/kGw7D8RV/kwQ=="},
			{DigestType.SHA512, HASH_COMPOSITION_1, SEED_1, TEXT_2, "Vl/uDdsdwbu8iTV9fA1+cbFKoVBUAa3sL3VGxE5h1qdxe5gFJSDUXWL4N9O4MnZWRR+jOEfgdtoQXURy3tx+Og=="},
			
			{DigestType.SHA512_256, HASH_COMPOSITION_1, SEED_1, TEXT_1, "TsRs4s1ZFlU3F+qdqKlBXS006pzAYMyswq7Ptq/hm08="},
			{DigestType.SHA512_256, HASH_COMPOSITION_1, SEED_2, TEXT_1, "sUiTXQ/YZT8TM8gj9JP8OiHK9Co53Qm9sjQrvKgUubc="},
			{DigestType.SHA512_256, HASH_COMPOSITION_1, SEED_1, TEXT_2, "snWoLYlLp8x1ED/wLX2GI9ueOHb4T+O6xf54xq4YIkw="},
			
			{DigestType.SHA3_256, HASH_COMPOSITION_1, SEED_1, TEXT_1, "47AGLVUDXe2xxYBVNMpvfs31/pMkNfLVyP7aGRV4SfA="},
			{DigestType.SHA3_256, HASH_COMPOSITION_1, SEED_2, TEXT_1, "mx5iefAq9owwm7araBk/LQRGhPXsrikBLsMzJOgaSbU="},
			{DigestType.SHA3_256, HASH_COMPOSITION_1, SEED_1, TEXT_2, "s4XHi26LJ/rsUpV1OV5XW3Fv0c+ae/zcCyi5htVMU00="},
			
			{DigestType.SHA3_384, HASH_COMPOSITION_1, SEED_1, TEXT_1, "J3O3T35Ge51wfJZig8l0SX4ty2R/ImDcaAlaYT38t2HVZcNP0jThFq+e4MQkLw+x"},
			{DigestType.SHA3_384, HASH_COMPOSITION_1, SEED_2, TEXT_1, "0ZuKZoN8t+aCwJehlrTMX1dCxcXlQubb0JZy0YUeaJgGEpSTl46JO59sVsFYCLJg"},
			{DigestType.SHA3_384, HASH_COMPOSITION_1, SEED_1, TEXT_2, "GY2jhqXDzP0vySjh+kWB/v0N1UfoqU0HDr7WYlJ3B55dYk3mT2IWfupQqrZ+BmEV"},
			
			{DigestType.SHA3_512, HASH_COMPOSITION_1, SEED_1, TEXT_1, "1OM9V5ESPkOvYETJIrcvaLUXzR2gLXBtjBi7s6dHsKIJGeOXTfATm4R6Ol3WAPZ5dylbKiyRUW+NTQUfp15ebQ=="},
			{DigestType.SHA3_512, HASH_COMPOSITION_1, SEED_2, TEXT_1, "NqFVpIp7bPEQbqfeJztmfKHZNobUZXBTXRKYAH1HQsWUnlAxCitP3dZ0NvLjQiDWv1Xq4cGrY2t2VKWzDSmGqg=="},
			{DigestType.SHA3_512, HASH_COMPOSITION_1, SEED_1, TEXT_2, "BQ0ykdPwWyFh78NQZa/xi14+Ixv+7Xh6Zg11hVskN4KoP2IIQAu5q6gNL0ipjb365NHrKcUciNvnA9O3jBYBsQ=="},
			
			{DigestType.SHAKE128, HASH_COMPOSITION_1, SEED_1, TEXT_1, "XZzskIacDaPSUjiuWXL3iveu1W9m8nNA4ypfO1cL1+I="},
			{DigestType.SHAKE128, HASH_COMPOSITION_1, SEED_2, TEXT_1, "cedbOjDPPP6o65GCK2GikE4imgmDozyknF9ZlsbDzPw="},
			{DigestType.SHAKE128, HASH_COMPOSITION_1, SEED_1, TEXT_2, "5jDZbYutjfnd5TW2uGIFUQ+uqT+HtgCZ9U84g4GlUYM="},
			
			{DigestType.SHAKE256, HASH_COMPOSITION_1, SEED_1, TEXT_1, "6rCab4rygXr5SWIiGuHTpHY4KXK2+0hj4/uSA2upjat+WUDQjEPDnLx19eckSGYRau4vAvVu7A2VQRL515MJGg=="},
			{DigestType.SHAKE256, HASH_COMPOSITION_1, SEED_2, TEXT_1, "dMTdTXBW3CEK+z6yCvRAa6eNI25ueDKAwNEXuaS3ww/w3ZDQPTMmSF8zbWhy/EKOyOsLeIhuMi0FrKJVNXPX5g=="},
			{DigestType.SHAKE256, HASH_COMPOSITION_1, SEED_1, TEXT_2, "gTM/P1SMtvNMUwL1YQuFZh4WKsu9dQSGD8aJx8sEEra6g1+03AzN2WgJZhY28pgttuU4BZrrlmWdapUsMCbuVQ=="},
			
	};
	
	public static void main(String[] args) throws UtilsException {
		for (Object[] row : getData()) {
			testDigestBase64((DigestType) row[0], (String) row[1], row[2], (String) row[3], (String) row[4]);
			testDigestBinary((DigestType) row[0], (String) row[1], row[2], (String) row[3], (String) row[4]);
		}
	}
	
	
	private static DigestConfig prepareConf(DigestType type, String hashComposition, byte[] seed) {
		DigestConfig conf = new DigestConfig();
		conf.setDigestType(type);
		conf.setAlgorithmSecureRandom("SHA1PRNG");
		conf.setBase64Encode(true);
		conf.setSaltLength(seed.length);
		conf.setHashComposition(hashComposition);
		return conf;
	}
	
	private static byte[] prepareSeed(Object seed) {
		if (seed instanceof Long)
			return ByteBuffer.allocate(Long.BYTES).putLong((Long) seed).array();
		if (seed instanceof byte[])
			return (byte[]) seed;
		if (seed instanceof Integer)
			return ByteBuffer.allocate(Long.BYTES).putInt((Integer) seed).array();
		return new byte[0];
	}
	
	public static byte[] testDigestBase64(DigestType type, String hashComposition, Object seed, String clear, String digest) throws UtilsException {
		byte[] seedBytes = prepareSeed(seed);
		DigestConfig config = prepareConf(type, hashComposition, seedBytes);
		config.setBase64Encode(true);
		
		return testDigest(config, seedBytes, clear.getBytes(), digest.getBytes());
	}
	
	public static byte[] testDigestBinary(DigestType type, String hashComposition, Object seed, String clear, String digest) throws UtilsException {
		byte[] seedBytes = prepareSeed(seed);
		DigestConfig config = prepareConf(type,  hashComposition, seedBytes);
		config.setBase64Encode(false);
		
		return testDigest(config, seedBytes, clear.getBytes(), Base64.getDecoder().decode(digest));
	}
	
	private static byte[] testDigest(DigestConfig config, byte[] seed, byte[] clear, byte[] digest) throws UtilsException {
		IDigest engine = DigestFactory.getDigest(LoggerFactory.getLogger(DigestTest.class), config);
		byte[] output = engine.digest(clear, seed);
		
		if (!Arrays.areEqual(output, digest))
			throw new UtilsException("digest: " + new String(digest) + " is not equals to " + new String(output));
		if (!engine.check(clear, seed, digest))
			throw new UtilsException("check digest failed for clear text: " + clear);
		
		return output;
	}
	
	public static Object[][] getData() {
		return TEST_DATA;
	}
}
