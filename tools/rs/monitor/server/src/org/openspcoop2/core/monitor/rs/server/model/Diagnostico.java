package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.DateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class Diagnostico  {
  
  @Schema(description = "")
  private DateTime data = null;
  public enum SeveritaEnum {
    ERRORINTEGRATION("errorIntegration"),
    ERRORPROTOCOL("errorProtocol"),
    INFOINTEGRATION("infoIntegration"),
    INFOPROTOCOL("InfoProtocol"),
    DEBUGLOW("debugLow"),
    DEBUGMEDIUM("debugMedium"),
    DEBUGHIGH("debugHigh");

    private String value;

    SeveritaEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static SeveritaEnum fromValue(String text) {
      for (SeveritaEnum b : SeveritaEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "errorProtocol", description = "")
  private SeveritaEnum severita = null;
  public enum FunzioneEnum {
    RICEZIONEBUSTE("ricezioneBuste"),
    RICEZIONECONTENUTIAPPLICATIVI("ricezioneContenutiApplicativi"),
    INOLTROBUSTE("inoltroBuste"),
    CONSEGNACONTENUTIAPPLICATIVI("consegnaContenutiApplicativi"),
    IMBUSTAMENTO("imbustamento"),
    SBUSTAMENTO("sbustamento");

    private String value;

    FunzioneEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static FunzioneEnum fromValue(String text) {
      for (FunzioneEnum b : FunzioneEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "ricezioneBuste", description = "")
  private FunzioneEnum funzione = null;
  
  @Schema(example = "Generato messaggio di cooperazione di Errore con identificativo [0998f497-e05d-420a-a6b0-ff3bb718d2c4]", description = "")
  private String messaggio = null;
 /**
   * Get data
   * @return data
  **/
  @JsonProperty("data")
  @Valid
  public DateTime getData() {
    return this.data;
  }

  public void setData(DateTime data) {
    this.data = data;
  }

  public Diagnostico data(DateTime data) {
    this.data = data;
    return this;
  }

 /**
   * Get severita
   * @return severita
  **/
  @JsonProperty("severita")
  @Valid
  public String getSeverita() {
    if (this.severita == null) {
      return null;
    }
    return this.severita.getValue();
  }

  public void setSeverita(SeveritaEnum severita) {
    this.severita = severita;
  }

  public Diagnostico severita(SeveritaEnum severita) {
    this.severita = severita;
    return this;
  }

 /**
   * Get funzione
   * @return funzione
  **/
  @JsonProperty("funzione")
  @Valid
  public String getFunzione() {
    if (this.funzione == null) {
      return null;
    }
    return this.funzione.getValue();
  }

  public void setFunzione(FunzioneEnum funzione) {
    this.funzione = funzione;
  }

  public Diagnostico funzione(FunzioneEnum funzione) {
    this.funzione = funzione;
    return this;
  }

 /**
   * Get messaggio
   * @return messaggio
  **/
  @JsonProperty("messaggio")
  @Valid
  public String getMessaggio() {
    return this.messaggio;
  }

  public void setMessaggio(String messaggio) {
    this.messaggio = messaggio;
  }

  public Diagnostico messaggio(String messaggio) {
    this.messaggio = messaggio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Diagnostico {\n");
    
    sb.append("    data: ").append(Diagnostico.toIndentedString(this.data)).append("\n");
    sb.append("    severita: ").append(Diagnostico.toIndentedString(this.severita)).append("\n");
    sb.append("    funzione: ").append(Diagnostico.toIndentedString(this.funzione)).append("\n");
    sb.append("    messaggio: ").append(Diagnostico.toIndentedString(this.messaggio)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
