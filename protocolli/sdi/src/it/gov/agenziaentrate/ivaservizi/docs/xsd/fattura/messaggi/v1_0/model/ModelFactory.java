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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.model;

/**     
 * Factory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModelFactory {

	public static RicevutaConsegnaTypeModel RICEVUTA_CONSEGNA_TYPE = new RicevutaConsegnaTypeModel();
	
	public static RicevutaScartoTypeModel RICEVUTA_SCARTO_TYPE = new RicevutaScartoTypeModel();
	
	public static RicevutaImpossibilitaRecapitoTypeModel RICEVUTA_IMPOSSIBILITA_RECAPITO_TYPE = new RicevutaImpossibilitaRecapitoTypeModel();
	
	public static FileMetadatiTypeModel FILE_METADATI_TYPE = new FileMetadatiTypeModel();
	

}