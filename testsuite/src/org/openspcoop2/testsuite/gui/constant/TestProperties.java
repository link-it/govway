/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.testsuite.gui.constant;
/**
 * Tipi enumerativi
 * 
 * Convenzione: i tipi che iniziano con _ indicano i valori delle proprieta
 * 
 *  Esempio
 *  _localhost("localhost")
 *  remote_core_host indica la proprieta:
 *  remote_core_host=localhost
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TestProperties {

	//field name properties
	remote_core_host,
	remote_core_port,
	remote_core_browser,
	openspcoop_http_endpoint,
	openspcoop_http_port,
	logger_file_name,
	logger_name,
	xml_registro,
	xml_config,
	regserv_context,
	pdd_context,
	ctrlstat_context,
	ge_context,
	wait_time,
	
	//value properties
	_gui_pdd_context("pdd"),
	_gui_regserv_context("regserv"),
	_gui_ctrlstat_context("pddConsole"),
	_gui_ge_context("ge"),
	_config_file_name("/gui_testsuite.properties"),
	_default_browser("*firefox"),
	_default_remote_core_port("6666"),
	_default_openspcoop_http_port("8080"),
	_default_logger_file_name("testsuite.log4j.properties"),
	_default_logger_name("GUITestSuite"),
	_WAIT_PAGE_TO_LOAD("30000");
	
	private String nome;
	private TestProperties(String nome) {
		this.nome=nome;
	}
	
	private TestProperties(){
		this.nome=this.name();
	}
	
	@Override
	public String toString(){
		return this.nome;
	}
}


