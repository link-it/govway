/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.lib.audit.dao;

import java.io.Serializable;
import java.util.ArrayList;

import org.openspcoop2.web.lib.audit.costanti.Costanti;

/**
 * Dao contenente i valori della Configurazione dell'audit
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Configurazione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5876374583726748177L;
	
	private boolean auditEngineEnabled = false;
	private boolean auditEnabled = false;
	private boolean dumpEnabled = false;
	private String dumpFormat = Costanti.DUMP_JSON_FORMAT;
	private ArrayList<Filtro> filtri = new ArrayList<Filtro>();
	private ArrayList<Appender> appender = new ArrayList<Appender>();
	
	public boolean isAuditEngineEnabled() {
		return this.auditEngineEnabled;
	}
	public void setAuditEngineEnabled(boolean auditEngineEnabled) {
		this.auditEngineEnabled = auditEngineEnabled;
	}
	public boolean isAuditEnabled() {
		return this.auditEnabled;
	}
	public void setAuditEnabled(boolean auditEnabled) {
		this.auditEnabled = auditEnabled;
	}
	public boolean isDumpEnabled() {
		return this.dumpEnabled;
	}
	public void setDumpEnabled(boolean dumpEnabled) {
		this.dumpEnabled = dumpEnabled;
	}
	public String getDumpFormat() {
		return this.dumpFormat;
	}
	public void setDumpFormat(String dumpFormat) {
		this.dumpFormat = dumpFormat;
	}
	
	public ArrayList<Filtro> getFiltri() {
		return this.filtri;
	}
	public void setFiltri(ArrayList<Filtro> filtri) {
		this.filtri = filtri;
	}
	public int sizeFiltri(){
		return this.filtri.size();
	}
	public Filtro getFiltro(int index){
		return this.filtri.get(index);
	}
	public Filtro removeFiltro(int index){
		return this.filtri.remove(index);
	}
	public void addFiltro(Filtro filtro){
		this.filtri.add(filtro);
	}
	
	public ArrayList<Appender> getAppender() {
		return this.appender;
	}
	public void setAppender(ArrayList<Appender> appender) {
		this.appender = appender;
	}
	public int sizeAppender(){
		return this.appender.size();
	}
	public Appender getAppender(int index){
		return this.appender.get(index);
	}
	public Appender removeAppender(int index){
		return this.appender.remove(index);
	}
	public void addAppender(Appender appender){
		this.appender.add(appender);
	}
}
