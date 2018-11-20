package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.DateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Diagnostico  {
  
  @Schema(description = "")
  private DateTime data = null;
@XmlType(name="SeveritaEnum")
@XmlEnum(String.class)
public enum SeveritaEnum {

@XmlEnumValue("errorIntegration") ERRORINTEGRATION(String.valueOf("errorIntegration")), @XmlEnumValue("errorProtocol") ERRORPROTOCOL(String.valueOf("errorProtocol")), @XmlEnumValue("infoIntegration") INFOINTEGRATION(String.valueOf("infoIntegration")), @XmlEnumValue("InfoProtocol") INFOPROTOCOL(String.valueOf("InfoProtocol")), @XmlEnumValue("debugLow") DEBUGLOW(String.valueOf("debugLow")), @XmlEnumValue("debugMedium") DEBUGMEDIUM(String.valueOf("debugMedium")), @XmlEnumValue("debugHigh") DEBUGHIGH(String.valueOf("debugHigh"));


    private String value;

    SeveritaEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static SeveritaEnum fromValue(String v) {
        for (SeveritaEnum b : SeveritaEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "errorProtocol", description = "")
  private SeveritaEnum severita = null;
@XmlType(name="FunzioneEnum")
@XmlEnum(String.class)
public enum FunzioneEnum {

@XmlEnumValue("ricezioneBuste") RICEZIONEBUSTE(String.valueOf("ricezioneBuste")), @XmlEnumValue("ricezioneContenutiApplicativi") RICEZIONECONTENUTIAPPLICATIVI(String.valueOf("ricezioneContenutiApplicativi")), @XmlEnumValue("inoltroBuste") INOLTROBUSTE(String.valueOf("inoltroBuste")), @XmlEnumValue("consegnaContenutiApplicativi") CONSEGNACONTENUTIAPPLICATIVI(String.valueOf("consegnaContenutiApplicativi")), @XmlEnumValue("imbustamento") IMBUSTAMENTO(String.valueOf("imbustamento")), @XmlEnumValue("sbustamento") SBUSTAMENTO(String.valueOf("sbustamento"));


    private String value;

    FunzioneEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static FunzioneEnum fromValue(String v) {
        for (FunzioneEnum b : FunzioneEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
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
  public String getSeverita() {
    if (this.severita == null) {
      return null;
    }
    return this.severita.value();
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
  public String getFunzione() {
    if (this.funzione == null) {
      return null;
    }
    return this.funzione.value();
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
