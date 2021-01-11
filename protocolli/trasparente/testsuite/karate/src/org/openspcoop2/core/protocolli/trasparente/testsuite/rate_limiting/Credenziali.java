/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting;

public class Credenziali {
	
	static public final CredenzialiBasic nonFiltrateSoggetto = new CredenzialiBasic("SoggettoNonFiltrato", "SoggettoNonFiltrato");
	static public final CredenzialiBasic filtrateSoggetto = new CredenzialiBasic("SoggettoFiltrato", "SoggettoFiltrato");
	
	static public final CredenzialiBasic nonFiltrateApplicativo = new CredenzialiBasic("ApplicativoNonFiltrato", "ApplicativoNonFiltrato");
	static public final CredenzialiBasic filtrateApplicativo = new CredenzialiBasic("ApplicativoFiltrato", "ApplicativoFiltrato");
	
	static public final CredenzialiBasic soggettoRuoloFiltrato = new CredenzialiBasic("SoggettoRuoloFiltrato", "SoggettoRuoloFiltrato");
	static public final CredenzialiBasic soggettoRuoloFiltrato2 = new CredenzialiBasic("SoggettoRuoloFiltrato2", "SoggettoRuoloFiltrato2");
	
	static public final CredenzialiBasic applicativoRuoloFiltrato = new CredenzialiBasic("ApplicativoRuoloFiltrato", "ApplicativoRuoloFiltrato");
	static public final CredenzialiBasic applicativoRuoloFiltrato2 = new CredenzialiBasic("ApplicativoRuoloFiltrato2", "ApplicativoRuoloFiltrato2");
	static public final CredenzialiBasic applicativoRuoloNonFiltrato = new CredenzialiBasic("ApplicativoRuoloNonFiltrato", "ApplicativoRuoloNonFiltrato");

	
	static public final CredenzialiBasic applicativoSITFFiltrato = new CredenzialiBasic("ApplicativoSoggettoInternoTestFruitoreFiltrato", "ApplicativoSoggettoInternoTestFruitoreFiltrato");
	static public final CredenzialiBasic applicativoSITFNonFiltrato = new CredenzialiBasic("ApplicativoSoggettoInternoTestFruitoreNonFiltrato", "ApplicativoSoggettoInternoTestFruitoreNonFiltrato");
	
	static public final CredenzialiBasic applicativoSITFRuoloFiltrato = new CredenzialiBasic("ApplicativoSoggettoInternoTestFruitoreRuoloFiltrato", "ApplicativoSoggettoInternoTestFruitoreRuoloFiltrato");
	static public final CredenzialiBasic applicativoSITFRuoloNonFiltrato = new CredenzialiBasic("ApplicativoSoggettoInternoTestFruitoreRuoloNonFiltrato", "ApplicativoSoggettoInternoTestFruitoreRuoloNonFiltrato");

}
