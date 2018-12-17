package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets StatoFunzionalitaConWarningEnum
 */
public enum StatoFunzionalitaConWarningEnum {
ABILITATO("abilitato"),
  DISABILITATO("disabilitato"),
  WARNINGONLY("warningOnly");

  private String value;

  StatoFunzionalitaConWarningEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static StatoFunzionalitaConWarningEnum fromValue(String text) {
    for (StatoFunzionalitaConWarningEnum b : StatoFunzionalitaConWarningEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
