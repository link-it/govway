/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.template_scan.cli;

import java.util.ArrayList;
import java.util.List;


/*  TreePath
*
* @author Burlon Tommaso (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TreePath implements Comparable<TreePath>{
	private final List<String> segments = new ArrayList<>();
	
	public static TreePath of(String ...segs) {
		TreePath res = new TreePath();
		res.segments.addAll(List.of(segs));
		return res;
	}
	
	public TreePath add(TreePath path) {
		this.segments.addAll(path.segments);
		return this;
	}
	
	public TreePath add(String ...segments) {
		this.add(TreePath.of(segments));
		return this;
	}
	

	@Override
	public int compareTo(TreePath o) {
		int i;
		for (i = 0; i < Math.min(o.segments.size(), this.segments.size()); i++) {
			int compare = (o.segments.get(i).compareTo(this.segments.get(i)));
			if (compare != 0)
				return compare;
		}
		return Integer.compare(o.segments.size(), this.segments.size());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TreePath))
			return false;
		TreePath t = (TreePath) o;
		return this.segments.equals(t.segments);
	}
	
	@Override
	public int hashCode() {
		return this.segments.hashCode();
	}
	
	
	@Override
	public String toString() {
		return String.join(" > ", this.segments);
	}
}
