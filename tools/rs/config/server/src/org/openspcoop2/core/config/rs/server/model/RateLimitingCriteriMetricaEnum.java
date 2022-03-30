/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
 * Gets or Sets RateLimitingCriteriMetricaEnum
 */
public enum RateLimitingCriteriMetricaEnum {
NUMERO_RICHIESTE("numero-richieste"),
  NUMERO_RICHIESTE_SIMULTANEE("numero-richieste-simultanee"),
  DIMENSIONE_MASSIMA("dimensione-massima"),
  OCCUPAZIONE_BANDA("occupazione-banda"),
  TEMPO_MEDIO_RISPOSTA("tempo-medio-risposta"),
  TEMPO_COMPLESSIVO_RISPOSTA("tempo-complessivo-risposta"),
  NUMERO_RICHIESTE_OK("numero-richieste-ok"),
  NUMERO_RICHIESTE_FALLITE("numero-richieste-fallite"),
  NUMERO_FAULT_APPLICATIVI("numero-fault-applicativi"),
  NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI("numero-richieste-fallite-o-fault-applicativi");

  private String value;

  RateLimitingCriteriMetricaEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static RateLimitingCriteriMetricaEnum fromValue(String text) {
    for (RateLimitingCriteriMetricaEnum b : RateLimitingCriteriMetricaEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
