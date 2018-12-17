package org.openspcoop2.core.config.rs.server.api;

import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiDescrizione;
import org.openspcoop2.core.config.rs.server.model.ApiInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiInterfaccia;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaView;
import org.openspcoop2.core.config.rs.server.model.ApiReferenteView;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.ApiServizio;
import org.openspcoop2.core.config.rs.server.model.ApiViewItem;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.ListaApi;
import org.openspcoop2.core.config.rs.server.model.ListaApiAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaApiAzioni;
import org.openspcoop2.core.config.rs.server.model.ListaApiRisorse;
import org.openspcoop2.core.config.rs.server.model.ListaApiServizi;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;

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
public interface ApiApi  {

    /**
     * Creazione di un&#x27;API
     *
     * Questa operazione consente di creare una API
     *
     */
    @POST
    @Path("/api")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un'API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "API creata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void create(@Valid Api body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Creazione di un allegato di una API
     *
     * Questa operazione consente di aggiungere un allegato alla API identificata dal nome e dalla versione
     *
     */
    @POST
    @Path("/api/{nome}/{versione}/allegati")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un allegato di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Allegato creato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createAllegato(@Valid ApiAllegato body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Creazione di un&#x27;azione di una API
     *
     * Questa operazione consente di aggiungere una azione al servizio della API identificata dal nome e dalla versione
     *
     */
    @POST
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}/azioni")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un'azione di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Azione creata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createAzione(@Valid ApiAzione body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Creazione di una risorsa di una API
     *
     * Questa operazione consente di aggiungere una risorsa alla API identificata dal nome e dalla versione
     *
     */
    @POST
    @Path("/api/{nome}/{versione}/risorse")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di una risorsa di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Risorsa creata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createRisorsa(@Valid ApiRisorsa body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Creazione di un servizio di una API
     *
     * Questa operazione consente di aggiungere un servizio alla API identificata dal nome e dalla versione
     *
     */
    @POST
    @Path("/api/{nome}/{versione}/servizi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un servizio di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Servizio creato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createServizio(@Valid ApiServizio body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina un&#x27;api
     *
     * Questa operazione consente di eliminare un API identificata dal nome e dalla versione
     *
     */
    @DELETE
    @Path("/api/{nome}/{versione}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina un'api", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "API eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void delete(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina un allegato di una API
     *
     * Questa operazione consente di eliminare un&#x27;allegato della API identificata dal nome e dalla versione
     *
     */
    @DELETE
    @Path("/api/{nome}/{versione}/allegati/{nome_allegato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina un allegato di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Allegato eliminato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteAllegato(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina un&#x27;azione del servizio di una API
     *
     * Questa operazione consente di eliminare un&#x27;azione del servizio della API identificata dal nome e dalla versione
     *
     */
    @DELETE
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}/azioni/{nome_azione}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina un'azione del servizio di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Azione eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteAzione(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @PathParam("nome_azione") String nomeAzione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina una risorsa di una API
     *
     * Questa operazione consente di eliminare una risorsa della API identificata dal nome e dalla versione
     *
     */
    @DELETE
    @Path("/api/{nome}/{versione}/risorse/{nome_risorsa}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina una risorsa di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Risorsa eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteRisorsa(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_risorsa") String nomeRisorsa, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina un servizio di una API
     *
     * Questa operazione consente di eliminare un servizio della API identificata dal nome e dalla versione
     *
     */
    @DELETE
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina un servizio di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Servizio eliminato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteServizio(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce l&#x27;allegato di una API
     *
     * Questa operazione consente di ottenere l&#x27;allegato di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/allegati/{nome_allegato}/download")
    @Produces({ "application/_*", "text/_*", "application/problem+json" })
    @Operation(summary = "Restituisce l'allegato di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Allegato dell'API restituito con successo", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] downloadAllegato(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce l&#x27;interfaccia di una API
     *
     * Questa operazione consente di ottenere l&#x27;interfaccia di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/interfaccia/download")
    @Produces({ "application/_*", "text/_*", "application/problem+json" })
    @Operation(summary = "Restituisce l'interfaccia di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Interfaccia dell'API restituita con successo", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] downloadInterfaccia(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Ricerca api
     *
     * Elenca le API registrate
     *
     */
    @GET
    @Path("/api")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca api", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaApi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaApi findAll(@QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("tipo_api") TipoApiEnum tipoApi);

    /**
     * Elenco allegati di una API
     *
     * Questa operazione consente di ottenere gli allegati di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/allegati")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Elenco allegati di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaApiAllegati.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaApiAllegati findAllAllegati(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset);

    /**
     * Elenco servizi di una API
     *
     * Questa operazione consente di ottenere le azioni di un servizio della API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}/azioni")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Elenco servizi di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaApiAzioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaApiAzioni findAllAzioni(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset);

    /**
     * Elenco risorse di una API
     *
     * Questa operazione consente di ottenere le risorse di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/risorse")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Elenco risorse di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaApiRisorse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaApiRisorse findAllRisorse(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("http_method") HttpMethodEnum httpMethod);

    /**
     * Elenco servizi di una API
     *
     * Questa operazione consente di ottenere i servizi di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/servizi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Elenco servizi di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaApiServizi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaApiServizi findAllServizi(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset);

    /**
     * Restituisce i dettagli di una API
     *
     * Questa operazione consente di ottenere i dettagli di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce i dettagli di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettagli di una API restituiti con successo", content = @Content(schema = @Schema(implementation = ApiViewItem.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiViewItem get(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce il dettaglio di un allegato di una API
     *
     * Questa operazione consente di ottenere il dettaglio di un allegato della API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/allegati/{nome_allegato}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di un allegato di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati dell'allegato restituiti con successo", content = @Content(schema = @Schema(implementation = ApiAllegato.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiAllegato getAllegato(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce il dettaglio di un&#x27;azione di un  servizio della API
     *
     * Questa operazione consente di ottenere il dettaglio di un&#x27;azione nel servizio della API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}/azioni/{nome_azione}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di un'azione di un  servizio della API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati dell'azione restituiti con successo", content = @Content(schema = @Schema(implementation = ApiAzione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiAzione getAzione(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @PathParam("nome_azione") String nomeAzione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce la descrizione di una API
     *
     * Questa operazione consente di ottenere la descrizione di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/descrizione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la descrizione di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della descrizione restituiti con successo", content = @Content(schema = @Schema(implementation = ApiDescrizione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiDescrizione getDescrizione(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce le informazioni generali di una API
     *
     * Questa operazione consente di ottenere le informazioni generali di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/informazioni")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni generali di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni generali restituite con successo", content = @Content(schema = @Schema(implementation = ApiInformazioniGeneraliView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiInformazioniGeneraliView getInformazioniGenerali(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce i dettagli dell&#x27;interfaccia di una API
     *
     * Questa operazione consente di ottenere i dettagli dell&#x27;interfaccia di una API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/interfaccia")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce i dettagli dell'interfaccia di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Interfaccia dell'API restituita con successo", content = @Content(schema = @Schema(implementation = ApiInterfacciaView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiInterfacciaView getInterfaccia(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce il nome del soggetto referente dell&#x27;api
     *
     * Questa operazione consente di ottenere il nome del soggetto referente dell&#x27;API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/referente")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il nome del soggetto referente dell'api", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Nome del soggetto referente restituito con successo", content = @Content(schema = @Schema(implementation = ApiReferenteView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiReferenteView getReferente(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce il dettaglio di una risorsa di una API
     *
     * Questa operazione consente di ottenere il dettaglio di una risorsa della API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/risorse/{nome_risorsa}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una risorsa di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della risorsa restituiti con successo", content = @Content(schema = @Schema(implementation = ApiRisorsa.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiRisorsa getRisorsa(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_risorsa") String nomeRisorsa, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce il dettaglio di un servizio di una API
     *
     * Questa operazione consente di ottenere il dettaglio di un servizio della API identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di un servizio di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati del servizio restituiti con successo", content = @Content(schema = @Schema(implementation = ApiServizio.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiServizio getServizio(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Modifica i dati di un allegato di una API
     *
     * Questa operazione consente di aggiornare i dettagli di un allegato della API identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/api/{nome}/{versione}/allegati/{nome_allegato}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di un allegato di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "L'allegato è stato aggiornato correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateAllegato(@Valid ApiAllegato body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Modifica i dati di un&#x27;azione nel servizio di una API
     *
     * Questa operazione consente di aggiornare i dettagli di un&#x27;azione della API identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}/azioni/{nome_azione}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di un'azione nel servizio di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "L'azione è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateAzione(@Valid ApiAzione body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @PathParam("nome_azione") String nomeAzione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare la descrizione di una API
     *
     * Questa operazione consente di aggiornare la descrizione di una API identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/api/{nome}/{versione}/descrizione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la descrizione di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La descrizione è stato aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateDescrizione(@Valid ApiDescrizione body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare le informazioni generali di una API
     *
     * Questa operazione consente di aggiornare le informazioni generali di una API identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/api/{nome}/{versione}/informazioni")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare le informazioni generali di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "API aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateInformazioniGenerali(@Valid ApiInformazioniGenerali body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare l&#x27;interfaccia di una API
     *
     * Questa operazione consente di aggiornare l&#x27;interfaccia di una API identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/api/{nome}/{versione}/interfaccia")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare l'interfaccia di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "L'interfaccia è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateInterfaccia(@Valid ApiInterfaccia body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Modifica i dati di una risorsa di una API
     *
     * Questa operazione consente di aggiornare i dettagli di una risorsa della API identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/api/{nome}/{versione}/risorse/{nome_risorsa}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una risorsa di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La risorsa è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateRisorsa(@Valid ApiRisorsa body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_risorsa") String nomeRisorsa, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Modifica i dati di un servizio di una API
     *
     * Questa operazione consente di aggiornare i dettagli di un servizio della API identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/api/{nome}/{versione}/servizi/{nome_servizio}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di un servizio di una API", tags={ "api" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Il servizio è stata aggiornato correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateServizio(@Valid ApiServizio body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_servizio") String nomeServizio, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);
}
