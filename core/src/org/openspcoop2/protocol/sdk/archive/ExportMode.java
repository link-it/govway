/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.archive;

/**
 *  ExportMode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExportMode extends ArchiveMode implements Cloneable {

	public ExportMode(String name) {
		super(name);
	}
	
	public ExportMode(ArchiveMode mode) {
		super(mode.getName());
	}
	
	public ExportMode(String name,boolean cascadeAllEnabled) {
		super(name);
		this.cascade = new ArchiveCascadeConfiguration(cascadeAllEnabled);
	}
	
	public ExportMode(ArchiveMode mode,boolean cascadeAllEnabled) {
		super(mode.getName());
		this.cascade = new ArchiveCascadeConfiguration(cascadeAllEnabled);
	}


	private ArchiveCascadeConfiguration cascade = new ArchiveCascadeConfiguration();


	public ArchiveCascadeConfiguration getCascade() {
		return this.cascade;
	}

	public void setCascade(ArchiveCascadeConfiguration cascade) {
		this.cascade = cascade;
	}

	@Override
	public Object clone(){
		ExportMode exportMode = new ExportMode(this.getName());
		if(this.cascade!=null)
			exportMode.setCascade((ArchiveCascadeConfiguration) this.cascade.clone());
		return exportMode;
	}

}
