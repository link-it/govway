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
package org.openspcoop2.core.monitor.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets EsitoTransazioneFullSearchEnum
 */
public enum EsitoTransazioneFullSearchEnum {
QUALSIASI("qualsiasi"),
  OK("ok"),
  FAULT("fault"),
  FALLITE("fallite"),
  FALLITE_E_FAULT("fallite_e_fault"),
  ERRORI_CONSEGNA("errori_consegna"),
  RICHIESTE_SCARTATE("richieste_scartate"),
  PERSONALIZZATO("personalizzato");

  private String value;

  EsitoTransazioneFullSearchEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static EsitoTransazioneFullSearchEnum fromValue(String text) {
    for (EsitoTransazioneFullSearchEnum b : EsitoTransazioneFullSearchEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
