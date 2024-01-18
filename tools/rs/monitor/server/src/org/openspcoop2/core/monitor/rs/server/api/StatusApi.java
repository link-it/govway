/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.monitor.rs.server.api;

import org.openspcoop2.core.monitor.rs.server.model.Problem;
import javax.ws.rs.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * GovWay Monitor API
 *
 * <p>Servizi per il monitoraggio di GovWay
 *
 */
@Path("/")
public interface StatusApi  {

    /**
     * Ritorna lo stato dell'applicazione
     *
     * Ritorna lo stato dell'applicazione in formato problem+json
     *
     */
    @GET
    @Path("/status")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Ritorna lo stato dell'applicazione", tags={ "Status" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Il servizio funziona correttamente", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Problem getStatus();
}
