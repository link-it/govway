<%--
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
--%>

<%@page import="org.openspcoop2.web.lib.mvc.GeneralData"%>
<%@page import="org.openspcoop2.web.lib.mvc.GeneralLink"%>
<%@page import="java.util.Vector"%>
<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf("multipart/form-data") != -1)) {
  iddati = (String) session.getAttribute("iddati");
} else {
  iddati = request.getParameter("iddati");
}
String gdString = "GeneralData";
if (iddati != null && !iddati.equals("notdefined"))
  gdString += iddati;
else
  iddati = "notdefined";
GeneralData gd = (GeneralData) session.getAttribute(gdString);
Vector<GeneralLink> v = gd.getHeaderLinks();

Vector<GeneralLink> modalitaLinks = gd.getModalitaLinks();
	if(v!= null && v.size() > 1) {
		
%>

<script type="text/javascript">
	function IEVersione (){
	    var ua = window.navigator.userAgent;
	
	    var msie = ua.indexOf('MSIE ');
	    if (msie > 0) {
	      // IE 10 or older => return version number
	      return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
	    }
	
	    var trident = ua.indexOf('Trident/');
	    if (trident > 0) {
	      // IE 11 => version
	      var rv = ua.indexOf('rv:');
	      return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
	    }
	
	    var edge = ua.indexOf('Edge/');
	    if (edge > 0) {
	      // Edge (IE 12+) => version
	      return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
	    }
	
	    return -1;
	}
	
	function isIE(){
	    return IEVersione() > -1;
	}
</script>
<script type="text/javascript">

//the direct source of the delay function in 1.4+
jQuery.fn.extend({
    delay: function( time, type ) {
        time = jQuery.fx ? jQuery.fx.speeds[time] || time : time;
        type = type || "fx";

        return this.queue( type, function() {
            var elem = this;
            setTimeout(function() {
                jQuery.dequeue( elem, type );
            }, time );
        });
    }
});

$(document).ready(function(){
	$('#menuUtente').hover(
	        function () {
	            //mostra sottomenu
	            // $('ul', this).stop(true, true).delay(50).slideDown(100); versione jquery > 1.3
	            //.animate({top: 0}, 50)
	            $('#menuUtente_menu', this).stop(true, true).delay(50).slideDown(100);
	        }, 
	        function () {
	            //nascondi sottomenu
	            $('#menuUtente_menu', this).stop(true, true).delay(150).slideUp(200);        
	        }
	    );
	
	$('#menuUtente_menu span[class*="icon-check"]').click(function() {
		var destinazione = $( this ).parent().children('span[class*="label"]').children().attr('href');
		window.location = destinazione;
	});
	
<% if(modalitaLinks!= null && modalitaLinks.size() > 1) { %>
		// menu modalita
		$('#menuModalita').hover(
		        function () {
		            //mostra sottomenu
		            // $('ul', this).stop(true, true).delay(50).slideDown(100); versione jquery > 1.3
		            //.animate({top: 0}, 50)
		            $('#menuModalita_menu', this).stop(true, true).delay(50).slideDown(100);
		        }, 
		        function () {
		            //nascondi sottomenu
		            $('#menuModalita_menu', this).stop(true, true).delay(150).slideUp(200);        
		        }
		    );
		
		$('#menuModalita_menu span[class*="icon-check"]').click(function() {
			var destinazione = $( this ).parent().children('span[class*="label"]').children().attr('href');
			window.location = destinazione;
		});
<% } %>
	
	if(isIE()){
		if(IEVersione() == 10){
			$('#menuUtente_menu span[class*="icon-check"]').attr('data-useragent', navigator.userAgent);
		}
	}
});
</script>

<%
	}
%>