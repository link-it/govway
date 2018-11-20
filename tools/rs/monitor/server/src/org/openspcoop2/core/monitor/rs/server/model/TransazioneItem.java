package org.openspcoop2.core.monitor.rs.server.model;

import java.util.UUID;
import org.joda.time.DateTime;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransazioneItem  {
  
  @Schema(example = "f54d8638-79f5-45db-878a-82d858ba128e", required = true, description = "")
  private UUID id = null;
  
  @Schema(required = true, description = "")
  private DateTime dataIngressoRichiesta = null;
  
  @Schema(description = "")
  private DateTime dataUscitaRichiesta = null;
  
  @Schema(description = "")
  private DateTime dataIngressoRisposta = null;
  
  @Schema(description = "")
  private DateTime dataUscitaRisposta = null;
@XmlType(name="TipologiaEnum")
@XmlEnum(String.class)
public enum TipologiaEnum {

@XmlEnumValue("fruizione") FRUIZIONE(String.valueOf("fruizione")), @XmlEnumValue("erogazione") EROGAZIONE(String.valueOf("erogazione"));


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
  
  @Schema(example = "Errore di Protocollo", description = "")
  private String esito = null;
  
  @Schema(example = "EnteInterno", description = "")
  private String fruitoreSoggetto = null;
  
  @Schema(example = "EnteEsterno", description = "")
  private String erogatoreSoggetto = null;
  
  @Schema(example = "EnteInternoSPCoopIT", description = "")
  private String fruitorePdd = null;
  
  @Schema(example = "EnteEsternoSPCoopIT", description = "")
  private String erogatorePdd = null;
  
  @Schema(example = "Servizio1", description = "")
  private String servizio = null;
  
  @Schema(example = "Azione1", description = "")
  private String azione = null;
  
  @Schema(example = "1", description = "")
  private Integer versioneServizio = null;
@XmlType(name="ProtocolloEnum")
@XmlEnum(String.class)
public enum ProtocolloEnum {

@XmlEnumValue("trasparente") TRASPARENTE(String.valueOf("trasparente")), @XmlEnumValue("spcoop") SPCOOP(String.valueOf("spcoop")), @XmlEnumValue("sdi") SDI(String.valueOf("sdi")), @XmlEnumValue("edelivery") EDELIVERY(String.valueOf("edelivery"));


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
  
  @Schema(example = "OpenSPCoopEnterprisePdD", description = "")
  private String idPdd = null;
  
  @Schema(example = "PROXY/OpenSPCoopEnterprise", description = "")
  private String soggettoPdd = null;
  
  @Schema(example = "8 ms", description = "")
  private String latenzaServizio = null;
  
  @Schema(example = "12 ms", description = "")
  private String latenzaPorta = null;
  
  @Schema(example = "20 ms", description = "")
  private String latenzaTotale = null;
  
  @Schema(example = "AB234", description = "")
  private String idApplicativoRichiesta = null;
  
  @Schema(example = "141", description = "")
  private String idApplicativoRisposta = null;
  
  @Schema(description = "")
  private String idMessaggioRichiesta = null;
  
  @Schema(description = "")
  private String idMessaggioRisposta = null;
@XmlType(name="ProfiloCollaborazioneEnum")
@XmlEnum(String.class)
public enum ProfiloCollaborazioneEnum {

@XmlEnumValue("oneway") ONEWAY(String.valueOf("oneway")), @XmlEnumValue("sincrono") SINCRONO(String.valueOf("sincrono")), @XmlEnumValue("asincronoAsimmetrico") ASINCRONOASIMMETRICO(String.valueOf("asincronoAsimmetrico")), @XmlEnumValue("asincronoSimmetrico") ASINCRONOSIMMETRICO(String.valueOf("asincronoSimmetrico"));


    private String value;

    ProfiloCollaborazioneEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static ProfiloCollaborazioneEnum fromValue(String v) {
        for (ProfiloCollaborazioneEnum b : ProfiloCollaborazioneEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "oneway", description = "")
  private ProfiloCollaborazioneEnum profiloCollaborazione = null;
  
  @Schema(example = "applicativo1", description = "")
  private String servizioApplicativo = null;
  
  @Schema(description = "")
  private String dimensioniIngressoRichiesta = null;
  
  @Schema(description = "")
  private String dimensioniUscitaRichiesta = null;
  
  @Schema(description = "")
  private String dimensioniIngressoRisposta = null;
  
  @Schema(description = "")
  private String dimensioniUscitaRisposta = null;
  
  @Schema(description = "")
  private String duplicatiRichiesta = null;
  
  @Schema(description = "")
  private String duplicatiRisposta = null;
  
  @Schema(description = "")
  private String eventi = null;
  
  @Schema(description = "")
  private String stato = null;
  
  @Schema(example = "127.0.0.1", description = "")
  private String indirizzoClient = null;
  
  @Schema(description = "")
  private String xForwardedFor = null;
 /**
   * Get id
   * @return id
  **/
  @JsonProperty("id")
  @NotNull
  public UUID getId() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public TransazioneItem id(UUID id) {
    this.id = id;
    return this;
  }

 /**
   * Get dataIngressoRichiesta
   * @return dataIngressoRichiesta
  **/
  @JsonProperty("dataIngressoRichiesta")
  @NotNull
  public DateTime getDataIngressoRichiesta() {
    return this.dataIngressoRichiesta;
  }

  public void setDataIngressoRichiesta(DateTime dataIngressoRichiesta) {
    this.dataIngressoRichiesta = dataIngressoRichiesta;
  }

  public TransazioneItem dataIngressoRichiesta(DateTime dataIngressoRichiesta) {
    this.dataIngressoRichiesta = dataIngressoRichiesta;
    return this;
  }

 /**
   * Get dataUscitaRichiesta
   * @return dataUscitaRichiesta
  **/
  @JsonProperty("dataUscitaRichiesta")
  public DateTime getDataUscitaRichiesta() {
    return this.dataUscitaRichiesta;
  }

  public void setDataUscitaRichiesta(DateTime dataUscitaRichiesta) {
    this.dataUscitaRichiesta = dataUscitaRichiesta;
  }

  public TransazioneItem dataUscitaRichiesta(DateTime dataUscitaRichiesta) {
    this.dataUscitaRichiesta = dataUscitaRichiesta;
    return this;
  }

 /**
   * Get dataIngressoRisposta
   * @return dataIngressoRisposta
  **/
  @JsonProperty("dataIngressoRisposta")
  public DateTime getDataIngressoRisposta() {
    return this.dataIngressoRisposta;
  }

  public void setDataIngressoRisposta(DateTime dataIngressoRisposta) {
    this.dataIngressoRisposta = dataIngressoRisposta;
  }

  public TransazioneItem dataIngressoRisposta(DateTime dataIngressoRisposta) {
    this.dataIngressoRisposta = dataIngressoRisposta;
    return this;
  }

 /**
   * Get dataUscitaRisposta
   * @return dataUscitaRisposta
  **/
  @JsonProperty("dataUscitaRisposta")
  public DateTime getDataUscitaRisposta() {
    return this.dataUscitaRisposta;
  }

  public void setDataUscitaRisposta(DateTime dataUscitaRisposta) {
    this.dataUscitaRisposta = dataUscitaRisposta;
  }

  public TransazioneItem dataUscitaRisposta(DateTime dataUscitaRisposta) {
    this.dataUscitaRisposta = dataUscitaRisposta;
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

  public TransazioneItem tipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  public String getEsito() {
    return this.esito;
  }

  public void setEsito(String esito) {
    this.esito = esito;
  }

  public TransazioneItem esito(String esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Get fruitoreSoggetto
   * @return fruitoreSoggetto
  **/
  @JsonProperty("fruitoreSoggetto")
  public String getFruitoreSoggetto() {
    return this.fruitoreSoggetto;
  }

  public void setFruitoreSoggetto(String fruitoreSoggetto) {
    this.fruitoreSoggetto = fruitoreSoggetto;
  }

  public TransazioneItem fruitoreSoggetto(String fruitoreSoggetto) {
    this.fruitoreSoggetto = fruitoreSoggetto;
    return this;
  }

 /**
   * Get erogatoreSoggetto
   * @return erogatoreSoggetto
  **/
  @JsonProperty("erogatoreSoggetto")
  public String getErogatoreSoggetto() {
    return this.erogatoreSoggetto;
  }

  public void setErogatoreSoggetto(String erogatoreSoggetto) {
    this.erogatoreSoggetto = erogatoreSoggetto;
  }

  public TransazioneItem erogatoreSoggetto(String erogatoreSoggetto) {
    this.erogatoreSoggetto = erogatoreSoggetto;
    return this;
  }

 /**
   * Get fruitorePdd
   * @return fruitorePdd
  **/
  @JsonProperty("fruitorePdd")
  public String getFruitorePdd() {
    return this.fruitorePdd;
  }

  public void setFruitorePdd(String fruitorePdd) {
    this.fruitorePdd = fruitorePdd;
  }

  public TransazioneItem fruitorePdd(String fruitorePdd) {
    this.fruitorePdd = fruitorePdd;
    return this;
  }

 /**
   * Get erogatorePdd
   * @return erogatorePdd
  **/
  @JsonProperty("erogatorePdd")
  public String getErogatorePdd() {
    return this.erogatorePdd;
  }

  public void setErogatorePdd(String erogatorePdd) {
    this.erogatorePdd = erogatorePdd;
  }

  public TransazioneItem erogatorePdd(String erogatorePdd) {
    this.erogatorePdd = erogatorePdd;
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

  public TransazioneItem servizio(String servizio) {
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

  public TransazioneItem azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get versioneServizio
   * minimum: 1
   * @return versioneServizio
  **/
  @JsonProperty("versioneServizio")
 @Min(1)  public Integer getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public TransazioneItem versioneServizio(Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
    return this;
  }

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

  public TransazioneItem protocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
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

  public TransazioneItem contesto(ContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get idPdd
   * @return idPdd
  **/
  @JsonProperty("idPdd")
  public String getIdPdd() {
    return this.idPdd;
  }

  public void setIdPdd(String idPdd) {
    this.idPdd = idPdd;
  }

  public TransazioneItem idPdd(String idPdd) {
    this.idPdd = idPdd;
    return this;
  }

 /**
   * Get soggettoPdd
   * @return soggettoPdd
  **/
  @JsonProperty("soggettoPdd")
  public String getSoggettoPdd() {
    return this.soggettoPdd;
  }

  public void setSoggettoPdd(String soggettoPdd) {
    this.soggettoPdd = soggettoPdd;
  }

  public TransazioneItem soggettoPdd(String soggettoPdd) {
    this.soggettoPdd = soggettoPdd;
    return this;
  }

 /**
   * Get latenzaServizio
   * @return latenzaServizio
  **/
  @JsonProperty("latenzaServizio")
  public String getLatenzaServizio() {
    return this.latenzaServizio;
  }

  public void setLatenzaServizio(String latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
  }

  public TransazioneItem latenzaServizio(String latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
    return this;
  }

 /**
   * Get latenzaPorta
   * @return latenzaPorta
  **/
  @JsonProperty("latenzaPorta")
  public String getLatenzaPorta() {
    return this.latenzaPorta;
  }

  public void setLatenzaPorta(String latenzaPorta) {
    this.latenzaPorta = latenzaPorta;
  }

  public TransazioneItem latenzaPorta(String latenzaPorta) {
    this.latenzaPorta = latenzaPorta;
    return this;
  }

 /**
   * Get latenzaTotale
   * @return latenzaTotale
  **/
  @JsonProperty("latenzaTotale")
  public String getLatenzaTotale() {
    return this.latenzaTotale;
  }

  public void setLatenzaTotale(String latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
  }

  public TransazioneItem latenzaTotale(String latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
    return this;
  }

 /**
   * Get idApplicativoRichiesta
   * @return idApplicativoRichiesta
  **/
  @JsonProperty("idApplicativoRichiesta")
  public String getIdApplicativoRichiesta() {
    return this.idApplicativoRichiesta;
  }

  public void setIdApplicativoRichiesta(String idApplicativoRichiesta) {
    this.idApplicativoRichiesta = idApplicativoRichiesta;
  }

  public TransazioneItem idApplicativoRichiesta(String idApplicativoRichiesta) {
    this.idApplicativoRichiesta = idApplicativoRichiesta;
    return this;
  }

 /**
   * Get idApplicativoRisposta
   * @return idApplicativoRisposta
  **/
  @JsonProperty("idApplicativoRisposta")
  public String getIdApplicativoRisposta() {
    return this.idApplicativoRisposta;
  }

  public void setIdApplicativoRisposta(String idApplicativoRisposta) {
    this.idApplicativoRisposta = idApplicativoRisposta;
  }

  public TransazioneItem idApplicativoRisposta(String idApplicativoRisposta) {
    this.idApplicativoRisposta = idApplicativoRisposta;
    return this;
  }

 /**
   * Get idMessaggioRichiesta
   * @return idMessaggioRichiesta
  **/
  @JsonProperty("idMessaggioRichiesta")
  public String getIdMessaggioRichiesta() {
    return this.idMessaggioRichiesta;
  }

  public void setIdMessaggioRichiesta(String idMessaggioRichiesta) {
    this.idMessaggioRichiesta = idMessaggioRichiesta;
  }

  public TransazioneItem idMessaggioRichiesta(String idMessaggioRichiesta) {
    this.idMessaggioRichiesta = idMessaggioRichiesta;
    return this;
  }

 /**
   * Get idMessaggioRisposta
   * @return idMessaggioRisposta
  **/
  @JsonProperty("idMessaggioRisposta")
  public String getIdMessaggioRisposta() {
    return this.idMessaggioRisposta;
  }

  public void setIdMessaggioRisposta(String idMessaggioRisposta) {
    this.idMessaggioRisposta = idMessaggioRisposta;
  }

  public TransazioneItem idMessaggioRisposta(String idMessaggioRisposta) {
    this.idMessaggioRisposta = idMessaggioRisposta;
    return this;
  }

 /**
   * Get profiloCollaborazione
   * @return profiloCollaborazione
  **/
  @JsonProperty("profiloCollaborazione")
  public String getProfiloCollaborazione() {
    if (this.profiloCollaborazione == null) {
      return null;
    }
    return this.profiloCollaborazione.value();
  }

  public void setProfiloCollaborazione(ProfiloCollaborazioneEnum profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
  }

  public TransazioneItem profiloCollaborazione(ProfiloCollaborazioneEnum profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
    return this;
  }

 /**
   * Get servizioApplicativo
   * @return servizioApplicativo
  **/
  @JsonProperty("servizioApplicativo")
  public String getServizioApplicativo() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativo(String servizioApplicativo) {
    this.servizioApplicativo = servizioApplicativo;
  }

  public TransazioneItem servizioApplicativo(String servizioApplicativo) {
    this.servizioApplicativo = servizioApplicativo;
    return this;
  }

 /**
   * Get dimensioniIngressoRichiesta
   * @return dimensioniIngressoRichiesta
  **/
  @JsonProperty("dimensioniIngressoRichiesta")
  public String getDimensioniIngressoRichiesta() {
    return this.dimensioniIngressoRichiesta;
  }

  public void setDimensioniIngressoRichiesta(String dimensioniIngressoRichiesta) {
    this.dimensioniIngressoRichiesta = dimensioniIngressoRichiesta;
  }

  public TransazioneItem dimensioniIngressoRichiesta(String dimensioniIngressoRichiesta) {
    this.dimensioniIngressoRichiesta = dimensioniIngressoRichiesta;
    return this;
  }

 /**
   * Get dimensioniUscitaRichiesta
   * @return dimensioniUscitaRichiesta
  **/
  @JsonProperty("dimensioniUscitaRichiesta")
  public String getDimensioniUscitaRichiesta() {
    return this.dimensioniUscitaRichiesta;
  }

  public void setDimensioniUscitaRichiesta(String dimensioniUscitaRichiesta) {
    this.dimensioniUscitaRichiesta = dimensioniUscitaRichiesta;
  }

  public TransazioneItem dimensioniUscitaRichiesta(String dimensioniUscitaRichiesta) {
    this.dimensioniUscitaRichiesta = dimensioniUscitaRichiesta;
    return this;
  }

 /**
   * Get dimensioniIngressoRisposta
   * @return dimensioniIngressoRisposta
  **/
  @JsonProperty("dimensioniIngressoRisposta")
  public String getDimensioniIngressoRisposta() {
    return this.dimensioniIngressoRisposta;
  }

  public void setDimensioniIngressoRisposta(String dimensioniIngressoRisposta) {
    this.dimensioniIngressoRisposta = dimensioniIngressoRisposta;
  }

  public TransazioneItem dimensioniIngressoRisposta(String dimensioniIngressoRisposta) {
    this.dimensioniIngressoRisposta = dimensioniIngressoRisposta;
    return this;
  }

 /**
   * Get dimensioniUscitaRisposta
   * @return dimensioniUscitaRisposta
  **/
  @JsonProperty("dimensioniUscitaRisposta")
  public String getDimensioniUscitaRisposta() {
    return this.dimensioniUscitaRisposta;
  }

  public void setDimensioniUscitaRisposta(String dimensioniUscitaRisposta) {
    this.dimensioniUscitaRisposta = dimensioniUscitaRisposta;
  }

  public TransazioneItem dimensioniUscitaRisposta(String dimensioniUscitaRisposta) {
    this.dimensioniUscitaRisposta = dimensioniUscitaRisposta;
    return this;
  }

 /**
   * Get duplicatiRichiesta
   * @return duplicatiRichiesta
  **/
  @JsonProperty("duplicatiRichiesta")
  public String getDuplicatiRichiesta() {
    return this.duplicatiRichiesta;
  }

  public void setDuplicatiRichiesta(String duplicatiRichiesta) {
    this.duplicatiRichiesta = duplicatiRichiesta;
  }

  public TransazioneItem duplicatiRichiesta(String duplicatiRichiesta) {
    this.duplicatiRichiesta = duplicatiRichiesta;
    return this;
  }

 /**
   * Get duplicatiRisposta
   * @return duplicatiRisposta
  **/
  @JsonProperty("duplicatiRisposta")
  public String getDuplicatiRisposta() {
    return this.duplicatiRisposta;
  }

  public void setDuplicatiRisposta(String duplicatiRisposta) {
    this.duplicatiRisposta = duplicatiRisposta;
  }

  public TransazioneItem duplicatiRisposta(String duplicatiRisposta) {
    this.duplicatiRisposta = duplicatiRisposta;
    return this;
  }

 /**
   * Get eventi
   * @return eventi
  **/
  @JsonProperty("eventi")
  public String getEventi() {
    return this.eventi;
  }

  public void setEventi(String eventi) {
    this.eventi = eventi;
  }

  public TransazioneItem eventi(String eventi) {
    this.eventi = eventi;
    return this;
  }

 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  public String getStato() {
    return this.stato;
  }

  public void setStato(String stato) {
    this.stato = stato;
  }

  public TransazioneItem stato(String stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get indirizzoClient
   * @return indirizzoClient
  **/
  @JsonProperty("indirizzoClient")
  public String getIndirizzoClient() {
    return this.indirizzoClient;
  }

  public void setIndirizzoClient(String indirizzoClient) {
    this.indirizzoClient = indirizzoClient;
  }

  public TransazioneItem indirizzoClient(String indirizzoClient) {
    this.indirizzoClient = indirizzoClient;
    return this;
  }

 /**
   * Get xForwardedFor
   * @return xForwardedFor
  **/
  @JsonProperty("X-Forwarded-For")
  public String getXForwardedFor() {
    return this.xForwardedFor;
  }

  public void setXForwardedFor(String xForwardedFor) {
    this.xForwardedFor = xForwardedFor;
  }

  public TransazioneItem xForwardedFor(String xForwardedFor) {
    this.xForwardedFor = xForwardedFor;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneItem {\n");
    
    sb.append("    id: ").append(TransazioneItem.toIndentedString(this.id)).append("\n");
    sb.append("    dataIngressoRichiesta: ").append(TransazioneItem.toIndentedString(this.dataIngressoRichiesta)).append("\n");
    sb.append("    dataUscitaRichiesta: ").append(TransazioneItem.toIndentedString(this.dataUscitaRichiesta)).append("\n");
    sb.append("    dataIngressoRisposta: ").append(TransazioneItem.toIndentedString(this.dataIngressoRisposta)).append("\n");
    sb.append("    dataUscitaRisposta: ").append(TransazioneItem.toIndentedString(this.dataUscitaRisposta)).append("\n");
    sb.append("    tipologia: ").append(TransazioneItem.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(TransazioneItem.toIndentedString(this.esito)).append("\n");
    sb.append("    fruitoreSoggetto: ").append(TransazioneItem.toIndentedString(this.fruitoreSoggetto)).append("\n");
    sb.append("    erogatoreSoggetto: ").append(TransazioneItem.toIndentedString(this.erogatoreSoggetto)).append("\n");
    sb.append("    fruitorePdd: ").append(TransazioneItem.toIndentedString(this.fruitorePdd)).append("\n");
    sb.append("    erogatorePdd: ").append(TransazioneItem.toIndentedString(this.erogatorePdd)).append("\n");
    sb.append("    servizio: ").append(TransazioneItem.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(TransazioneItem.toIndentedString(this.azione)).append("\n");
    sb.append("    versioneServizio: ").append(TransazioneItem.toIndentedString(this.versioneServizio)).append("\n");
    sb.append("    protocollo: ").append(TransazioneItem.toIndentedString(this.protocollo)).append("\n");
    sb.append("    contesto: ").append(TransazioneItem.toIndentedString(this.contesto)).append("\n");
    sb.append("    idPdd: ").append(TransazioneItem.toIndentedString(this.idPdd)).append("\n");
    sb.append("    soggettoPdd: ").append(TransazioneItem.toIndentedString(this.soggettoPdd)).append("\n");
    sb.append("    latenzaServizio: ").append(TransazioneItem.toIndentedString(this.latenzaServizio)).append("\n");
    sb.append("    latenzaPorta: ").append(TransazioneItem.toIndentedString(this.latenzaPorta)).append("\n");
    sb.append("    latenzaTotale: ").append(TransazioneItem.toIndentedString(this.latenzaTotale)).append("\n");
    sb.append("    idApplicativoRichiesta: ").append(TransazioneItem.toIndentedString(this.idApplicativoRichiesta)).append("\n");
    sb.append("    idApplicativoRisposta: ").append(TransazioneItem.toIndentedString(this.idApplicativoRisposta)).append("\n");
    sb.append("    idMessaggioRichiesta: ").append(TransazioneItem.toIndentedString(this.idMessaggioRichiesta)).append("\n");
    sb.append("    idMessaggioRisposta: ").append(TransazioneItem.toIndentedString(this.idMessaggioRisposta)).append("\n");
    sb.append("    profiloCollaborazione: ").append(TransazioneItem.toIndentedString(this.profiloCollaborazione)).append("\n");
    sb.append("    servizioApplicativo: ").append(TransazioneItem.toIndentedString(this.servizioApplicativo)).append("\n");
    sb.append("    dimensioniIngressoRichiesta: ").append(TransazioneItem.toIndentedString(this.dimensioniIngressoRichiesta)).append("\n");
    sb.append("    dimensioniUscitaRichiesta: ").append(TransazioneItem.toIndentedString(this.dimensioniUscitaRichiesta)).append("\n");
    sb.append("    dimensioniIngressoRisposta: ").append(TransazioneItem.toIndentedString(this.dimensioniIngressoRisposta)).append("\n");
    sb.append("    dimensioniUscitaRisposta: ").append(TransazioneItem.toIndentedString(this.dimensioniUscitaRisposta)).append("\n");
    sb.append("    duplicatiRichiesta: ").append(TransazioneItem.toIndentedString(this.duplicatiRichiesta)).append("\n");
    sb.append("    duplicatiRisposta: ").append(TransazioneItem.toIndentedString(this.duplicatiRisposta)).append("\n");
    sb.append("    eventi: ").append(TransazioneItem.toIndentedString(this.eventi)).append("\n");
    sb.append("    stato: ").append(TransazioneItem.toIndentedString(this.stato)).append("\n");
    sb.append("    indirizzoClient: ").append(TransazioneItem.toIndentedString(this.indirizzoClient)).append("\n");
    sb.append("    xForwardedFor: ").append(TransazioneItem.toIndentedString(this.xForwardedFor)).append("\n");
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
