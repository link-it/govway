package org.openspcoop2.core.config.rs.server.api;

import org.openspcoop2.core.config.rs.server.model.ApiImplStato;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneApplicativi;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneApplicativo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuoli;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuolo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScope;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScopes;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneSoggetti;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneSoggetto;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneView;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiGestioneToken;
import org.openspcoop2.core.config.rs.server.model.GestioneCors;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;

import javax.ws.rs.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
@Path("/")
public interface ErogazioniConfigurazioneApi  {

    /**
     * Aggiunta di applicativi all&#x27;elenco degli applicativi autorizzati puntualmente
     *
     * Questa operazione consente di aggiungere applicativi all&#x27;elenco degli applicativi autorizzati puntualmente
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di applicativi all'elenco degli applicativi autorizzati puntualmente", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Applicativi aggiunti con con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addControlloAccessiAutorizzazionePuntualeApplicativi(@Valid ControlloAccessiAutorizzazioneApplicativo body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Aggiunta di soggetti all&#x27;elenco dei soggetti autorizzati puntualmente
     *
     * Questa operazione consente di aggiungere soggetti all&#x27;elenco dei soggetti autorizzati puntualmente
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/soggetti")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di soggetti all'elenco dei soggetti autorizzati puntualmente", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Soggetti aggiunti con con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addControlloAccessiAutorizzazionePuntualeSoggetti(@Valid ControlloAccessiAutorizzazioneSoggetto body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Aggiunta di ruoli all&#x27;elenco dei ruoli autorizzati
     *
     * Questa operazione consente di aggiungere ruoli all&#x27;elenco dei ruoli autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di ruoli all'elenco dei ruoli autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Ruoli aggiunti con con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addControlloAccessiAutorizzazioneRuoli(@Valid ControlloAccessiAutorizzazioneRuolo body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Aggiunta di scope all&#x27;elenco degli scope autorizzati
     *
     * Questa operazione consente di aggiungere scope all&#x27;elenco degli scope autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di scope all'elenco degli scope autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Scope aggiunti con con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addControlloAccessiAutorizzazioneScope(@Valid ControlloAccessiAutorizzazioneScope body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Elimina applicativi dall&#x27;elenco degli applicativi autorizzati puntualmente
     *
     * Questa operazione consente di eliminare applicativi dall&#x27;elenco degli applicativi autorizzati puntualmente
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi/{applicativo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina applicativi dall'elenco degli applicativi autorizzati puntualmente", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Applicativi eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteControlloAccessiAutorizzazionePuntualeApplicativi(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("applicativo_autorizzato") String applicativoAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Elimina soggetti all&#x27;elenco dei soggetti autorizzati puntualmente
     *
     * Questa operazione consente di eliminare soggetti all&#x27;elenco dei soggetti autorizzati puntualmente
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/soggetti/{soggetto_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina soggetti all'elenco dei soggetti autorizzati puntualmente", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Soggetti eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteControlloAccessiAutorizzazionePuntualeSoggetti(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("soggetto_autorizzato") String soggettoAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Elimina ruoli dall&#x27;elenco dei ruoli autorizzati
     *
     * Questa operazione consente di eliminare ruoli dall&#x27;elenco dei ruoli autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli/{ruolo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina ruoli dall'elenco dei ruoli autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Ruoli eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteControlloAccessiAutorizzazioneRuoli(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("ruolo_autorizzato") String ruoloAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Elimina scope dall&#x27;elenco degli scope autorizzati
     *
     * Questa operazione consente di eliminare scope dall&#x27;elenco degli scope autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope/{scope_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina scope dall'elenco degli scope autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Scope eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteControlloAccessiAutorizzazioneScope(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("scope_autorizzato") String scopeAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce la policy XACML associata all&#x27;autorizzazione
     *
     * Questa operazione consente di ottenere la policy XACML associata all&#x27;autorizzazione
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/download-xacml-policy")
    @Produces({ "application/xml", "application/problem+json" })
    @Operation(summary = "Restituisce la policy XACML associata all'autorizzazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Interfaccia dell'API restituita con successo", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] downloadControlloAccessiAutorizzazioneXacmlPolicy(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce la configurazione relativa all&#x27;autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all&#x27;autenticazione per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autenticazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutenticazione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutenticazione getControlloAccessiAutenticazione(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce la configurazione relativa all&#x27;autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all&#x27;autorizzazione per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneView getControlloAccessiAutorizzazione(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce l&#x27;elenco degli applicativi autorizzati puntualmente
     *
     * Questa operazione consente di ottenere l&#x27;elenco degli applicativi autorizzati puntualmente
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco degli applicativi autorizzati puntualmente", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco applicativi autorizzati restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneApplicativi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneApplicativi getControlloAccessiAutorizzazionePuntualeApplicativi(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce l&#x27;elenco dei soggetti autorizzati puntualmente
     *
     * Questa operazione consente di ottenere l&#x27;elenco dei soggetti autorizzati puntualmente
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/soggetti")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco dei soggetti autorizzati puntualmente", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco soggetti autorizzati restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneSoggetti.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneSoggetti getControlloAccessiAutorizzazionePuntualeSoggetti(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce l&#x27;elenco dei ruoli autorizzati
     *
     * Questa operazione consente di ottenere l&#x27;elenco dei ruoli autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco dei ruoli autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco ruoli autorizzati restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneRuoli.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneRuoli getControlloAccessiAutorizzazioneRuoli(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce l&#x27;elenco degli scope autorizzati
     *
     * Questa operazione consente di ottenere l&#x27;elenco degli scope autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco degli scope autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco scope autorizzati restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneScopes.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneScopes getControlloAccessiAutorizzazioneScope(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/gestione-token")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiGestioneToken.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiGestioneToken getControlloAccessiGestioneToken(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce le informazioni sulla configurazione CORS associata all&#x27;erogazione
     *
     * Questa operazione consente di ottenere le informazioni sulla configurazione CORS associata all&#x27;erogazione identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/gestione-cors")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni sulla configurazione CORS associata all'erogazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sulla configurazione CORS restituite con successo", content = @Content(schema = @Schema(implementation = GestioneCors.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public GestioneCors getGestioneCORS(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce l&#x27;indicazione sullo stato del gruppo
     *
     * Questa operazione consente di ottenere lo stato attuale del gruppo
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/stato")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'indicazione sullo stato del gruppo", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio dello stato restituito con successo", content = @Content(schema = @Schema(implementation = ApiImplStato.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplStato getStato(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Consente di modificare la configurazione relativa all&#x27;autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all&#x27;autenticazione per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autenticazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateControlloAccessiAutenticazione(@Valid ControlloAccessiAutenticazione body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Consente di modificare la configurazione relativa all&#x27;autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all&#x27;autorizzazione per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateControlloAccessiAutorizzazione(@Valid ControlloAccessiAutorizzazione body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Consente di modificare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/gestione-token")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateControlloAccessiGestioneToken(@Valid ControlloAccessiGestioneToken body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Consente di modificare la configurazione CORS associata all&#x27;erogazione
     *
     * Questa operazione consente di aggiornare la configurazione CORS associata all&#x27;erogazione identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/gestione-cors")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione CORS associata all'erogazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione dell'erogazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateGestioneCORS(@Valid GestioneCors body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare lo stato del gruppo
     *
     * Questa operazione consente di aggiornare lo stato del gruppo
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/stato")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare lo stato del gruppo", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione del gruppo aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateStato(@Valid ApiImplStato body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo);
}
