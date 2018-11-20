package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets FonteEnum
 */
public enum FonteEnum {
QUALSIASI("qualsiasi"),
  REGISTRO("registro"),
  ESTERNA("esterna");

  private String value;

  FonteEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static FonteEnum fromValue(String text) {
    for (FonteEnum b : FonteEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
