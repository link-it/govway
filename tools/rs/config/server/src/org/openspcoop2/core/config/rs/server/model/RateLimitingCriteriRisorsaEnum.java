/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
 * Gets or Sets RateLimitingCriteriRisorsaEnum
 */
public enum RateLimitingCriteriRisorsaEnum {
NUMERO_RICHIESTE("numero-richieste"),
  NUMERO_RICHIESTE_SIMULTANEE("numero-richieste-simultanee"),
  OCCUPAZIONE_BANDA("occupazione-banda"),
  TEMPO_MEDIO_RISPOSTA("tempo-medio-risposta"),
  TEMPO_COMPLESSIVO_RISPOSTA("tempo-complessivo-risposta");

  private String value;

  RateLimitingCriteriRisorsaEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static RateLimitingCriteriRisorsaEnum fromValue(String text) {
    for (RateLimitingCriteriRisorsaEnum b : RateLimitingCriteriRisorsaEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
