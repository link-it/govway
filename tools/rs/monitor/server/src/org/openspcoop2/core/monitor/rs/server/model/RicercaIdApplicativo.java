package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RicercaIdApplicativo extends FiltroTemporale {
@XmlType(name="ProtocolloEnum")
@XmlEnum(String.class)
public enum ProtocolloEnum {

@XmlEnumValue("spcoop") SPCOOP(String.valueOf("spcoop")), @XmlEnumValue("trasparente") TRASPARENTE(String.valueOf("trasparente")), @XmlEnumValue("sdi") SDI(String.valueOf("sdi")), @XmlEnumValue("edelivery") EDELIVERY(String.valueOf("edelivery"));


    private String value;

    ProtocolloEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static ProtocolloEnum fromValue(String v) {
        for (ProtocolloEnum b : ProtocolloEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "trasparente", description = "")
  private ProtocolloEnum protocollo = null;
  
  @Schema(example = "EnteInterno", description = "")
  private String soggettoLocale = null;
  
  @Schema(example = "EnteEsterno", description = "")
  private String soggettoRemoto = null;
  
  @Schema(example = "applicativo1", description = "")
  private String applicativo = null;
  
  @Schema(example = "servizio1", description = "")
  private String servizio = null;
  
  @Schema(example = "azione1", description = "")
  private String azione = null;
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
  
  @Schema(description = "")
  private Boolean ricercaEsatta = null;
  
  @Schema(description = "")
  private Boolean caseSensitive = null;
  
  @Schema(example = "abc123", description = "")
  private String idApplicativo = null;
 /**
   * Get protocollo
   * @return protocollo
  **/
  @JsonProperty("protocollo")
  public String getProtocollo() {
    if (this.protocollo == null) {
      return null;
    }
    return this.protocollo.value();
  }

  public void setProtocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
  }

  public RicercaIdApplicativo protocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get soggettoLocale
   * @return soggettoLocale
  **/
  @JsonProperty("soggettoLocale")
  public String getSoggettoLocale() {
    return this.soggettoLocale;
  }

  public void setSoggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
  }

  public RicercaIdApplicativo soggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
    return this;
  }

 /**
   * Get soggettoRemoto
   * @return soggettoRemoto
  **/
  @JsonProperty("soggettoRemoto")
  public String getSoggettoRemoto() {
    return this.soggettoRemoto;
  }

  public void setSoggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
  }

  public RicercaIdApplicativo soggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  public String getApplicativo() {
    return this.applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public RicercaIdApplicativo applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }

 /**
   * Get servizio
   * @return servizio
  **/
  @JsonProperty("servizio")
  public String getServizio() {
    return this.servizio;
  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public RicercaIdApplicativo servizio(String servizio) {
    this.servizio = servizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public RicercaIdApplicativo azione(String azione) {
    this.azione = azione;
    return this;
  }

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

  public RicercaIdApplicativo tipologia(TipologiaEnum tipologia) {
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

  public RicercaIdApplicativo esito(EsitoEnum esito) {
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

  public RicercaIdApplicativo dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
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

  public RicercaIdApplicativo contesto(ContestoEnum contesto) {
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

  public RicercaIdApplicativo evento(String evento) {
    this.evento = evento;
    return this;
  }

 /**
   * Get ricercaEsatta
   * @return ricercaEsatta
  **/
  @JsonProperty("ricercaEsatta")
  public Boolean isisRicercaEsatta() {
    return this.ricercaEsatta;
  }

  public void setRicercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
  }

  public RicercaIdApplicativo ricercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
    return this;
  }

 /**
   * Get caseSensitive
   * @return caseSensitive
  **/
  @JsonProperty("caseSensitive")
  public Boolean isisCaseSensitive() {
    return this.caseSensitive;
  }

  public void setCaseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public RicercaIdApplicativo caseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    return this;
  }

 /**
   * Get idApplicativo
   * @return idApplicativo
  **/
  @JsonProperty("idApplicativo")
  public String getIdApplicativo() {
    return this.idApplicativo;
  }

  public void setIdApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
  }

  public RicercaIdApplicativo idApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaIdApplicativo {\n");
    sb.append("    ").append(RicercaIdApplicativo.toIndentedString(super.toString())).append("\n");
    sb.append("    protocollo: ").append(RicercaIdApplicativo.toIndentedString(this.protocollo)).append("\n");
    sb.append("    soggettoLocale: ").append(RicercaIdApplicativo.toIndentedString(this.soggettoLocale)).append("\n");
    sb.append("    soggettoRemoto: ").append(RicercaIdApplicativo.toIndentedString(this.soggettoRemoto)).append("\n");
    sb.append("    applicativo: ").append(RicercaIdApplicativo.toIndentedString(this.applicativo)).append("\n");
    sb.append("    servizio: ").append(RicercaIdApplicativo.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(RicercaIdApplicativo.toIndentedString(this.azione)).append("\n");
    sb.append("    tipologia: ").append(RicercaIdApplicativo.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(RicercaIdApplicativo.toIndentedString(this.esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(RicercaIdApplicativo.toIndentedString(this.dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(RicercaIdApplicativo.toIndentedString(this.contesto)).append("\n");
    sb.append("    evento: ").append(RicercaIdApplicativo.toIndentedString(this.evento)).append("\n");
    sb.append("    ricercaEsatta: ").append(RicercaIdApplicativo.toIndentedString(this.ricercaEsatta)).append("\n");
    sb.append("    caseSensitive: ").append(RicercaIdApplicativo.toIndentedString(this.caseSensitive)).append("\n");
    sb.append("    idApplicativo: ").append(RicercaIdApplicativo.toIndentedString(this.idApplicativo)).append("\n");
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
