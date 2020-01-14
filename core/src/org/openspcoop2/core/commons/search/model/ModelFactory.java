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
package org.openspcoop2.core.commons.search.model;

/**     
 * Factory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModelFactory {

	public static SoggettoModel SOGGETTO = new SoggettoModel();
	
	public static PortaDominioModel PORTA_DOMINIO = new PortaDominioModel();
	
	public static GruppoModel GRUPPO = new GruppoModel();
	
	public static AccordoServizioParteComuneModel ACCORDO_SERVIZIO_PARTE_COMUNE = new AccordoServizioParteComuneModel();
	
	public static AccordoServizioParteComuneGruppoModel ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO = new AccordoServizioParteComuneGruppoModel();
	
	public static AccordoServizioParteComuneAzioneModel ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE = new AccordoServizioParteComuneAzioneModel();
	
	public static PortTypeModel PORT_TYPE = new PortTypeModel();
	
	public static OperationModel OPERATION = new OperationModel();
	
	public static ResourceModel RESOURCE = new ResourceModel();
	
	public static AccordoServizioParteSpecificaModel ACCORDO_SERVIZIO_PARTE_SPECIFICA = new AccordoServizioParteSpecificaModel();
	
	public static FruitoreModel FRUITORE = new FruitoreModel();
	
	public static ServizioApplicativoModel SERVIZIO_APPLICATIVO = new ServizioApplicativoModel();
	
	public static PortaDelegataModel PORTA_DELEGATA = new PortaDelegataModel();
	
	public static PortaApplicativaModel PORTA_APPLICATIVA = new PortaApplicativaModel();
	

}