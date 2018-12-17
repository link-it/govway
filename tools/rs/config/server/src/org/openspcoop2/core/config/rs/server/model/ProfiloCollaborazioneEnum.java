package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ProfiloCollaborazioneEnum
 */
public enum ProfiloCollaborazioneEnum {
ONEWAY("oneway"),
  SINCRONO("sincrono"),
  ASINCRONOSIMMETRICO("asincronoSimmetrico"),
  ASINCRONOASIMMETRICO("asincronoAsimmetrico");

  private String value;

  ProfiloCollaborazioneEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static ProfiloCollaborazioneEnum fromValue(String text) {
    for (ProfiloCollaborazioneEnum b : ProfiloCollaborazioneEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
