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
package org.openspcoop2.pdd.config;

import java.util.List;

import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.message.soap.mtom.MtomXomPackageInfo;

/**
 * MTOMProcessorConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMProcessorConfig {

	private List<MtomXomPackageInfo> info;
	private MTOMProcessorType mtomProcessorType;
	
	public List<MtomXomPackageInfo> getInfo() {
		return this.info;
	}
	public void setInfo(List<MtomXomPackageInfo> info) {
		this.info = info;
	}
	public MTOMProcessorType getMtomProcessorType() {
		return this.mtomProcessorType;
	}
	public void setMtomProcessorType(MTOMProcessorType mtomProcessorType) {
		this.mtomProcessorType = mtomProcessorType;
	}
	
}
