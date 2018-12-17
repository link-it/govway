package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoAutorizzazioneEnum
 */
public enum TipoAutorizzazioneEnum {
DISABILITATO("disabilitato"),
  ABILITATO("abilitato"),
  XACML_POLICY("xacml-Policy"),
  CUSTOM("custom");

  private String value;

  TipoAutorizzazioneEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoAutorizzazioneEnum fromValue(String text) {
    for (TipoAutorizzazioneEnum b : TipoAutorizzazioneEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
