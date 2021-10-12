/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.core.config.rs.server.api;

import org.openspcoop2.core.config.rs.server.model.ApiImplAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazione;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazioneView;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApi;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApiView;
import org.openspcoop2.core.config.rs.server.model.ConnettoreFruizione;
import java.io.File;
import org.openspcoop2.core.config.rs.server.model.Fruizione;
import org.openspcoop2.core.config.rs.server.model.FruizioneModI;
import org.openspcoop2.core.config.rs.server.model.FruizioneViewItem;
import org.openspcoop2.core.config.rs.server.model.ListaApiImplAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaFruizioni;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
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
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createFruizione(@Valid Fruizione body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Creazione di un allegato nella fruizione di API
     *
     * Questa operazione consente di aggiungere un allegato alla fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un allegato nella fruizione di API", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createFruizioneAllegato(@Valid ApiImplAllegato body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina una fruizione di api
     *
     * Questa operazione consente di eliminare una fruizione di API identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizione(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina un allegato dalla fruizione
     *
     * Questa operazione consente di eliminare un'allegato dalla fruizione di API identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneAllegato(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'allegato di una fruizione
     *
     * Questa operazione consente di ottenere l'allegato di un'erogazione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/allegati/{nome_allegato}/download")
    @Produces({ "application/_*", "text/_*", "application/problem+json" })
    @Operation(summary = "Restituisce l'allegato di una fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Allegato della fruizione restituito con successo", content = @Content(schema = @Schema(implementation = File.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] downloadFruizioneAllegato(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elenco allegati di una fruizione di API
     *
     * Questa operazione consente di ottenere gli allegati di una fruizione di API identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaApiImplAllegati findAllFruizioneAllegati(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset);

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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaFruizioni findAllFruizioni(@QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset, @QueryParam("tipo_api") TipoApiEnum tipoApi, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("uri_api_implementata") @Pattern(regexp="^[a-z]{2,20}/[0-9A-Za-z]+:[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$|^[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$") String uriApiImplementata, @QueryParam("profilo_qualsiasi") @DefaultValue("false") Boolean profiloQualsiasi, @QueryParam("soggetto_qualsiasi") @DefaultValue("false") Boolean soggettoQualsiasi);

    /**
     * Restituisce i dettagli di una fruizione di API
     *
     * Questa operazione consente di ottenere i dettagli di una fruizione di API identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public FruizioneViewItem getFruizione(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le informazioni sull'API implementata dalla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sull'API implementata dall'erogazione identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplVersioneApiView getFruizioneAPI(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di un allegato della fruizione
     *
     * Questa operazione consente di ottenere il dettaglio di un allegato della fruizione di API identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplAllegato getFruizioneAllegato(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le informazioni su connettore associato alla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sul connettore associato alla fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/connettore")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni su connettore associato alla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sul connettore restituite con successo", content = @Content(schema = @Schema(implementation = ConnettoreFruizione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ConnettoreFruizione getFruizioneConnettore(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Restituisce le informazioni generali di una fruizione di API
     *
     * Questa operazione consente di ottenere le informazioni generali di una fruizione di API identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplInformazioniGeneraliView getFruizioneInformazioniGenerali(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le informazioni ModI associate alla fruizione
     *
     * Questa operazione consente di ottenere le informazioni ModI associate all'erogazione identificata dall'erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/modi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni ModI associate alla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni ModI restituite con successo", content = @Content(schema = @Schema(implementation = FruizioneModI.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public FruizioneModI getFruizioneModI(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le informazioni sull'url di invocazione necessaria ad invocare la fruizione
     *
     * Questa operazione consente di ottenere le informazioni sull'url di invocazione necessaria ad invocare la fruizione identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplUrlInvocazioneView getFruizioneUrlInvocazione(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la versione dell'API implementata dalla fruizione
     *
     * Questa operazione consente di aggiornare la versione dell'API implementata dall'erogazione identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneAPI(@Valid ApiImplVersioneApi body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di un allegato della fruizione
     *
     * Questa operazione consente di aggiornare i dettagli di un allegato della fruizione di API identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneAllegato(@Valid ApiImplAllegato body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_allegato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeAllegato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione del connettore associato alla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione del connettore associato alla fruizione identificata dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneConnettore(@Valid ConnettoreFruizione body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("gruppo") @Size(max=255) String gruppo);

    /**
     * Consente di modificare le informazioni generali di una fruizione di API
     *
     * Questa operazione consente di aggiornare le informazioni generali di una fruizione di API identificata  dall'erogatore, dal nome e dalla versione
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneInformazioniGenerali(@Valid ApiImplInformazioniGenerali body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare le informazioni ModI associate alla fruizione
     *
     * Questa operazione consente di aggiornare le informazioni ModI associate all'erogazione identificata dall'erogatore, dal nome e dalla versione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/modi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare le informazioni ModI associate alla fruizione", tags={ "fruizioni" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Fruizione di API aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneModI(@Valid FruizioneModI body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione utilizzata per identificare l'azione invocata dell'API implementata dalla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione utilizzata dal Gateway per identificare l'azione invocata
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
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneUrlInvocazione(@Valid ApiImplUrlInvocazione body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);
}
