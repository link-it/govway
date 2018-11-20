package org.openspcoop2.core.monitor.rs.server.api;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.openspcoop2.core.monitor.rs.server.model.Fault;
import org.openspcoop2.core.monitor.rs.server.model.GenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.ReportGrafico;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * GovWay Monitor API
 *
 * <p>Servizi per il monitoraggio di GovWay
 *
 */
@Path("/")
public interface ReportisticaApi  {

    /**
     * genera report statistico
     *
     * Questa operazione consente di generare un report statistico
     *
     */
    @POST
    @Path("/statistiche/report")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "genera report statistico", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "report generato correttamente", content = @Content(schema = @Schema(implementation = ReportGrafico.class))),
        @ApiResponse(responseCode = "400", description = "La richiesta inviata ha prodotto un errore", content = @Content(schema = @Schema(implementation = Fault.class))) })
    public ReportGrafico generaReport(@Valid GenerazioneReport body);
}
