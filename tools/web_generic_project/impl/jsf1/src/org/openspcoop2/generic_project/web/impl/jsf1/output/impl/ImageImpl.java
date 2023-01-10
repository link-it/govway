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
package org.openspcoop2.generic_project.web.impl.jsf1.output.impl;

import org.openspcoop2.generic_project.web.impl.jsf1.utils.Jsf1Utils;
import org.openspcoop2.generic_project.web.output.Image;
import org.openspcoop2.generic_project.web.output.OutputType;

/**
 * Implementazione di un elemento di output di tipo Image/Icon.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class ImageImpl extends TextImpl implements Image {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ImageImpl(){
		super();
		this.setType(OutputType.IMAGE); 
		this.setInsideGroup(false);
	}
	
	private String image = null;
	private String title = null;
	private String alt = null;
	
 

	@Override
	public String getImage() {
		return this.image;
	}

	@Override
	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getTitle() {
		try{
			String tmp = Jsf1Utils.getInstance().getMessageFromResourceBundle(this.title);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.title;
	}

	@Override
	public String getAlt() {
		try{
			String tmp = Jsf1Utils.getInstance().getMessageFromResourceBundle(this.alt);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.alt;
	}
	@Override
	public void setAlt(String alt) {
		this.alt = alt;
	}

}
