/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.costanti;

import org.openspcoop2.web.ctrlstat.core.OperazioneDaSmistare;

/**
 * Tipi utilizzati come parametri nelle {@link OperazioneDaSmistare}
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum OperationsParameter {

	// Soggetto
	NOME_SOGGETTO("NomeSoggetto"), TIPO_SOGGETTO("TipoSoggetto"), OLD_NOME_SOGGETTO("OldNomeSoggetto"), OLD_TIPO_SOGGETTO("OldTipoSoggetto"),
	// Servizio
	NOME_SERVIZIO("NomeServizio"), TIPO_SERVIZIO("TipoServizio"), OLD_NOME_SERVIZIO("OldNomeServizio"), OLD_TIPO_SERVIZIO("OldTipoServizio"),
	// PA
	NOME_PA("NomePA"), OLD_NOME_PA("OldNomePA"),
	// PD
	NOME_PD("NomePD"), OLD_NOME_PD("OldNomePD"),
	// ServizioApplicativo
	NOME_SERVIZIO_APPLICATIVO("NomeServizioApplicativo"), OLD_NOME_SERVIZIO_APPLICATIVO("OldNomeServizioApplicativo"),
	// Accordo
	NOME_ACCORDO("NomeAccordo"), OLD_NOME_ACCORDO("OldNomeAccordo"),
	VERSIONE_ACCORDO("VersioneAccordo"), OLD_VERSIONE_ACCORDO("OldVersioneAccordo"),
	NOME_REFERENTE("NomeSoggetto"), TIPO_REFERENTE("TipoSoggetto"), OLD_NOME_REFERENTE("OldNomeSoggetto"), OLD_TIPO_REFERENTE("OldTipoSoggetto"),
	// Fruitore
	ID_FRUITORE("IDFruitore"), NOME_FRUITORE("NomeFruitore"), TIPO_FRUITORE("TipoFruitore"),
	// PoliticheSicurezza
	PS_ID_FRUITORE("IDFruitore"), PS_ID_SERVIZIO("IDServizio"), PS_ID_SERVIZIO_APPLICATIVO("IDServizioApplicativo"),

	// ID Table
	ID_TABLE("IDTable"),

	// Oggetto
	OGGETTO("Oggetto"), PORTA_DOMINIO("PortaDominio");

	private final String nome;

	OperationsParameter(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return this.nome;
	}
}
