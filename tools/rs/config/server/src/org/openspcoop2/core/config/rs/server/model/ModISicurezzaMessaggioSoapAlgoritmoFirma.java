/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ModISicurezzaMessaggioSoapAlgoritmoFirma
 */
public enum ModISicurezzaMessaggioSoapAlgoritmoFirma {
  DSA_SHA_256("DSA-SHA-256"),
  
  ECDSA_SHA_256("ECDSA-SHA-256"),
  
  ECDSA_SHA_384("ECDSA-SHA-384"),
  
  ECDSA_SHA_512("ECDSA-SHA-512"),
  
  RSA_SHA_256("RSA-SHA-256"),
  
  RSA_SHA_384("RSA-SHA-384"),
  
  RSA_SHA_512("RSA-SHA-512");

  private String value;

  ModISicurezzaMessaggioSoapAlgoritmoFirma(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static ModISicurezzaMessaggioSoapAlgoritmoFirma fromValue(String text) {
    for (ModISicurezzaMessaggioSoapAlgoritmoFirma b : ModISicurezzaMessaggioSoapAlgoritmoFirma.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
