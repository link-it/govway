package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FiltroTransazione  {
@XmlType(name="TipologiaEnum")
@XmlEnum(String.class)
public enum TipologiaEnum {

@XmlEnumValue("fruzione") FRUZIONE(String.valueOf("fruzione")), @XmlEnumValue("erogazione") EROGAZIONE(String.valueOf("erogazione"));


    private String value;

    TipologiaEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static TipologiaEnum fromValue(String v) {
        for (TipologiaEnum b : TipologiaEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "fruizione", description = "")
  private TipologiaEnum tipologia = null;
@XmlType(name="EsitoEnum")
@XmlEnum(String.class)
public enum EsitoEnum {

@XmlEnumValue("fallite") FALLITE(String.valueOf("fallite")), @XmlEnumValue("fault applicativo") FAULT_APPLICATIVO(String.valueOf("fault applicativo")), @XmlEnumValue("completate con successo") COMPLETATE_CON_SUCCESSO(String.valueOf("completate con successo"));


    private String value;

    EsitoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static EsitoEnum fromValue(String v) {
        for (EsitoEnum b : EsitoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "fallite", description = "")
  private EsitoEnum esito = null;
@XmlType(name="DettaglioEsitoEnum")
@XmlEnum(String.class)
public enum DettaglioEsitoEnum {

@XmlEnumValue("ok") OK(String.valueOf("ok")), @XmlEnumValue("ok (presenza anomalie") OK_PRESENZA_ANOMALIE(String.valueOf("ok (presenza anomalie")), @XmlEnumValue("fault applicativo") FAULT_APPLICATIVO(String.valueOf("fault applicativo")), @XmlEnumValue("autenticazione fallita") AUTENTICAZIONE_FALLITA(String.valueOf("autenticazione fallita")), @XmlEnumValue("autorizzazione negata") AUTORIZZAZIONE_NEGATA(String.valueOf("autorizzazione negata")), @XmlEnumValue("richiesta client rifiutata") RICHIESTA_CLIENT_RIFIUTATA(String.valueOf("richiesta client rifiutata")), @XmlEnumValue("errore di connessione") ERRORE_DI_CONNESSIONE(String.valueOf("errore di connessione")), @XmlEnumValue("errore di protocollo") ERRORE_DI_PROTOCOLLO(String.valueOf("errore di protocollo")), @XmlEnumValue("violazione rate limiting") VIOLAZIONE_RATE_LIMITING(String.valueOf("violazione rate limiting")), @XmlEnumValue("violazione rate limiting warningOnly") VIOLAZIONE_RATE_LIMITING_WARNINGONLY(String.valueOf("violazione rate limiting warningOnly")), @XmlEnumValue("superamento limite richieste") SUPERAMENTO_LIMITE_RICHIESTE(String.valueOf("superamento limite richieste")), @XmlEnumValue("superamento limite richieste warningOnly") SUPERAMENTO_LIMITE_RICHIESTE_WARNINGONLY(String.valueOf("superamento limite richieste warningOnly")), @XmlEnumValue("fault pdd esterna") FAULT_PDD_ESTERNA(String.valueOf("fault pdd esterna")), @XmlEnumValue("contenuto richiesta non riconosciuto") CONTENUTO_RICHIESTA_NON_RICONOSCIUTO(String.valueOf("contenuto richiesta non riconosciuto")), @XmlEnumValue("contenuto risposta non riconosciuto") CONTENUTO_RISPOSTA_NON_RICONOSCIUTO(String.valueOf("contenuto risposta non riconosciuto")), @XmlEnumValue("connessione client interrotta") CONNESSIONE_CLIENT_INTERROTTA(String.valueOf("connessione client interrotta")), @XmlEnumValue("fault generato dalla pdd") FAULT_GENERATO_DALLA_PDD(String.valueOf("fault generato dalla pdd")), @XmlEnumValue("errore interno pdd") ERRORE_INTERNO_PDD(String.valueOf("errore interno pdd"));


    private String value;

    DettaglioEsitoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static DettaglioEsitoEnum fromValue(String v) {
        for (DettaglioEsitoEnum b : DettaglioEsitoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "ok", description = "")
  private DettaglioEsitoEnum dettaglioEsito = null;
@XmlType(name="ContestoEnum")
@XmlEnum(String.class)
public enum ContestoEnum {

@XmlEnumValue("applicativo") APPLICATIVO(String.valueOf("applicativo")), @XmlEnumValue("sistema") SISTEMA(String.valueOf("sistema"));


    private String value;

    ContestoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static ContestoEnum fromValue(String v) {
        for (ContestoEnum b : ContestoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "applicativo", description = "")
  private ContestoEnum contesto = null;
  
  @Schema(example = "evento1", description = "")
  private String evento = null;
 /**
   * Get tipologia
   * @return tipologia
  **/
  @JsonProperty("tipologia")
  public String getTipologia() {
    if (this.tipologia == null) {
      return null;
    }
    return this.tipologia.value();
  }

  public void setTipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public FiltroTransazione tipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  public String getEsito() {
    if (this.esito == null) {
      return null;
    }
    return this.esito.value();
  }

  public void setEsito(EsitoEnum esito) {
    this.esito = esito;
  }

  public FiltroTransazione esito(EsitoEnum esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Get dettaglioEsito
   * @return dettaglioEsito
  **/
  @JsonProperty("dettaglioEsito")
  public String getDettaglioEsito() {
    if (this.dettaglioEsito == null) {
      return null;
    }
    return this.dettaglioEsito.value();
  }

  public void setDettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
  }

  public FiltroTransazione dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  public String getContesto() {
    if (this.contesto == null) {
      return null;
    }
    return this.contesto.value();
  }

  public void setContesto(ContestoEnum contesto) {
    this.contesto = contesto;
  }

  public FiltroTransazione contesto(ContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get evento
   * @return evento
  **/
  @JsonProperty("evento")
  public String getEvento() {
    return this.evento;
  }

  public void setEvento(String evento) {
    this.evento = evento;
  }

  public FiltroTransazione evento(String evento) {
    this.evento = evento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroTransazione {\n");
    
    sb.append("    tipologia: ").append(FiltroTransazione.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(FiltroTransazione.toIndentedString(this.esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(FiltroTransazione.toIndentedString(this.dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(FiltroTransazione.toIndentedString(this.contesto)).append("\n");
    sb.append("    evento: ").append(FiltroTransazione.toIndentedString(this.evento)).append("\n");
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
