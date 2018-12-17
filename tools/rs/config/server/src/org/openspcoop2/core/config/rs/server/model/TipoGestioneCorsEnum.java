package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoGestioneCorsEnum
 */
public enum TipoGestioneCorsEnum {
DISABILITATO("disabilitato"),
  GATEWAY("gateway"),
  APPLICATIVO("applicativo");

  private String value;

  TipoGestioneCorsEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoGestioneCorsEnum fromValue(String text) {
    for (TipoGestioneCorsEnum b : TipoGestioneCorsEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
