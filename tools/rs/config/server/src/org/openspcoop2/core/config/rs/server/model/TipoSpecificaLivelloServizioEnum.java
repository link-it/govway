package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoSpecificaLivelloServizioEnum
 */
public enum TipoSpecificaLivelloServizioEnum {
WS_AGREEMENT("WS-Agreement"),
  WSLA("WSLA");

  private String value;

  TipoSpecificaLivelloServizioEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoSpecificaLivelloServizioEnum fromValue(String text) {
    for (TipoSpecificaLivelloServizioEnum b : TipoSpecificaLivelloServizioEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
