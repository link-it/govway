/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.generic_project.web.impl.jsf1.output.impl;

import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;
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
			String tmp = Utils.getInstance().getMessageFromResourceBundle(this.title);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.title;
	}

	@Override
	public String getAlt() {
		try{
			String tmp = Utils.getInstance().getMessageFromResourceBundle(this.alt);

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
