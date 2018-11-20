package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ContestoEnum
 */
public enum ContestoEnum {
QUALSIASI("qualsiasi"),
  EROGAZIONE("erogazione"),
  FRUIZIONE("fruizione");

  private String value;

  ContestoEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static ContestoEnum fromValue(String text) {
    for (ContestoEnum b : ContestoEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
