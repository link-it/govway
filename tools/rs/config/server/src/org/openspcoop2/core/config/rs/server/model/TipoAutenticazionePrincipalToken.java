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
package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoAutenticazionePrincipalToken
 */
public enum TipoAutenticazionePrincipalToken {
SUBJECT("subject"),
  CLIENTID("clientId"),
  USERNAME("username"),
  EMAIL("eMail"),
  CUSTOM("custom");

  private String value;

  TipoAutenticazionePrincipalToken(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoAutenticazionePrincipalToken fromValue(String text) {
    for (TipoAutenticazionePrincipalToken b : TipoAutenticazionePrincipalToken.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
