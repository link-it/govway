package org.openspcoop2.core.config.rs.server.api;

import org.openspcoop2.core.config.rs.server.model.ApiImplAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazioneView;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApi;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApiView;
import org.openspcoop2.core.config.rs.server.model.Connettore;
import org.openspcoop2.core.config.rs.server.model.Fruizione;
import org.openspcoop2.core.config.rs.server.model.FruizioneViewItem;
import org.openspcoop2.core.config.rs.server.model.ListaApiImplAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaFruizioni;
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
public interface FruizioniApi  {

    /**
     * Creazione di una fruizione di API
     *
     * Questa operazione consente di creare una fruizione di API
     *
     */
    @POST
    @Path("/fruizioni")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di una fruizione di API", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Fruizione creata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void create(@Valid Fruizione body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Creazione di un allegato nella fruizione di API
     *
     * Questa operazione consente di aggiungere un allegato alla fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un allegato nella fruizione di API", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Allegato creato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createAllegato(@Valid ApiImplAllegato body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina una fruizione di api
     *
     * Questa operazione consente di eliminare una fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina una fruizione di api", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Fruizione di API eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void delete(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina un allegato dalla fruizione
     *
     * Questa operazione consente di eliminare un&#x27;allegato dalla fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati/{nome_allegato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina un allegato dalla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Allegato eliminato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteAllegato(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce l&#x27;allegato di una fruizione
     *
     * Questa operazione consente di ottenere l&#x27;allegato di un&#x27;erogazione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati/{nome_allegato}/download")
    @Produces({ "application/_*", "text/_*", "application/problem+json" })
    @Operation(summary = "Restituisce l'allegato di una fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Allegato della fruizione restituito con successo", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] downloadAllegato(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Ricerca fruizioni di api
     *
     * Elenca le fruizioni di API
     *
     */
    @GET
    @Path("/fruizioni")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca fruizioni di api", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaFruizioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaFruizioni findAll(@QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("tipo_api") TipoApiEnum tipoApi);

    /**
     * Elenco allegati di una fruizione di API
     *
     * Questa operazione consente di ottenere gli allegati di una fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Elenco allegati di una fruizione di API", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaApiImplAllegati.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaApiImplAllegati findAllAllegati(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset);

    /**
     * Restituisce i dettagli di una fruizione di API
     *
     * Questa operazione consente di ottenere i dettagli di una fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce i dettagli di una fruizione di API", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettagli di una fruizione di API restituiti con successo", content = @Content(schema = @Schema(implementation = FruizioneViewItem.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public FruizioneViewItem get(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce le informazioni sull&#x27;API implementata dalla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sull&#x27;API implementata dall&#x27;erogazione identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/api")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni sull'API implementata dalla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sull'API restituite con successo", content = @Content(schema = @Schema(implementation = ApiImplVersioneApiView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplVersioneApiView getAPI(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce il dettaglio di un allegato della fruizione
     *
     * Questa operazione consente di ottenere il dettaglio di un allegato della fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati/{nome_allegato}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di un allegato della fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati dell'allegato restituiti con successo", content = @Content(schema = @Schema(implementation = ApiImplAllegato.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplAllegato getAllegato(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce le informazioni su connettore associato alla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sul connettore associato alla fruizione identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/connettore")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni su connettore associato alla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sul connettore restituite con successo", content = @Content(schema = @Schema(implementation = Connettore.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Connettore getConnettore(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce le informazioni generali di una fruizione di API
     *
     * Questa operazione consente di ottenere le informazioni generali di una fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/informazioni")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni generali di una fruizione di API", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni generali restituite con successo", content = @Content(schema = @Schema(implementation = ApiImplInformazioniGeneraliView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplInformazioniGeneraliView getInformazioniGenerali(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Restituisce le informazioni sull&#x27;url di invocazione necessaria ad invocare la fruizione
     *
     * Questa operazione consente di ottenere le informazioni sull&#x27;url di invocazione necessaria ad invocare la fruizione identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/url-invocazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni sull'url di invocazione necessaria ad invocare la fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sull'url di invocazione restituite con successo", content = @Content(schema = @Schema(implementation = ApiImplUrlInvocazioneView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplUrlInvocazioneView getUrlInvocazione(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare la versione dell&#x27;API implementata dalla fruizione
     *
     * Questa operazione consente di aggiornare la versione dell&#x27;API implementata dall&#x27;erogazione identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/api")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la versione dell'API implementata dalla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Fruizione di API aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateAPI(@Valid ApiImplVersioneApi body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Modifica i dati di un allegato della fruizione
     *
     * Questa operazione consente di aggiornare i dettagli di un allegato della fruizione di API identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati/{nome_allegato}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di un allegato della fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "L'allegato è stato aggiornato correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateAllegato(@Valid ApiImplAllegato body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare la configurazione del connettore associato alla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione del connettore associato alla fruizione identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/connettore")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione del connettore associato alla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione della fruizione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateConnettore(@Valid Connettore body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare le informazioni generali di una fruizione di API
     *
     * Questa operazione consente di aggiornare le informazioni generali di una fruizione di API identificata  dall&#x27;erogatore, dal nome e dalla versione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/informazioni")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare le informazioni generali di una fruizione di API", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Fruizione di API aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateInformazioniGenerali(@Valid ApiImplInformazioniGenerali body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare la configurazione utilizzata per identificare l&#x27;azione invocata dell&#x27;API implementata dalla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione utilizzata dal Gateway per identificare l&#x27;azione invocata
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/url-invocazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione utilizzata per identificare l'azione invocata dell'API implementata dalla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione della fruizione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateUrlInvocazione(@Valid Object body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);
}
