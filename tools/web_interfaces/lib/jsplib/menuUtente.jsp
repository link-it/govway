<%--
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
--%>

<%@page import="org.openspcoop2.web.lib.mvc.Costanti"%>
<%@page import="org.openspcoop2.web.lib.mvc.ServletUtils"%>
<%@page import="org.openspcoop2.web.lib.mvc.GeneralData"%>
<%@page import="org.openspcoop2.web.lib.mvc.GeneralLink"%>
<%@page import="java.util.Vector"%>
<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf(Costanti.MULTIPART) != -1)) {
  iddati = ServletUtils.getObjectFromSession(request, session, String.class, Costanti.SESSION_ATTRIBUTE_ID_DATI);
} else {
  iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
}
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
if (iddati != null && !iddati.equals("notdefined"))
  gdString += iddati;
else
  iddati = "notdefined";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
Vector<GeneralLink> v = gd.getHeaderLinks();

Vector<GeneralLink> modalitaLinks = gd.getModalitaLinks();
Vector<GeneralLink> soggettoLinks = gd.getSoggettiLinks();

String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
if(v!= null && v.size() > 1) {
		
%>

<script type="text/javascript" nonce="<%= randomNonce %>">
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
<script type="text/javascript" nonce="<%= randomNonce %>">

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
	            $('#menuUtente').addClass('tastoMenuHover');
	            $('#menuUtente_menu', this).stop(true, true).delay(50).slideDown(100);
	        }, 
	        function () {
	            //nascondi sottomenu
	            $('#menuUtente').removeClass('tastoMenuHover');
	            $('#menuUtente_menu', this).stop(true, true).delay(150).slideUp(200);        
	        }
	    );
	
	$('#menuUtente_menu span[class*="item-icon"]').click(function() {
		var destinazione = $( this ).parent().children('span[class*="label"]').children().prop('href');
		if(destinazione) {
			// addTabID
			destinazione = addTabIdParam(destinazione);
			
			window.location = destinazione;
		}
	});
	
	$('#menuUtente_menu div[class*="menu-item"]').click(function() {
		var destinazione = $( this ).children('span[class*="label"]').children().prop('href');
		if(destinazione) {
			// addTabID
			destinazione = addTabIdParam(destinazione);
			
			window.location = destinazione;
		}
	});
	
	var paddLabelLeft = $(".item-label").css('padding-left');
	var paddLabelRight = $(".item-label").css('padding-right');
	var paddParentLabelLeft = $(".item-label").parent('.menu-item-no-icon').css('padding-left');
	var paddParentLabelRight = $(".item-label").parent('.menu-item-no-icon').css('padding-right');
	
<% if(modalitaLinks!= null && modalitaLinks.size() > 0) { 
	GeneralLink modalitaTitoloLink = modalitaLinks.get(0);
	if(modalitaLinks.size() == 1 && modalitaTitoloLink.getUrl().equals("")) {
		%>
		$('#menuModalita').css('cursor', 'default');
		<%
	}
		
	// visualizzo la tendina solo se ho piu' di due link (titolo + voci)
	if(modalitaLinks.size() > 1) { 
		
		int max = 0;
		// calcolo la lughezza massima della label
		for(int i = 1; i < modalitaLinks.size(); i++){
			if(modalitaLinks.get(i).getLabelWidth() > max)
				max = modalitaLinks.get(i).getLabelWidth();
		}
		
			%>
		
			var newItemWidth = <%=max %> + parseInt(paddLabelLeft) + parseInt(paddLabelRight) + parseInt(paddParentLabelLeft) + parseInt(paddParentLabelRight);
			
			// menu modalita
			$('#menuModalita').hover(
			        function () {
			            //mostra sottomenu
			            // $('ul', this).stop(true, true).delay(50).slideDown(100); versione jquery > 1.3
			            //.animate({top: 0}, 50)
			            $('#menuModalita').addClass('tastoMenuHover');
			            $('#menuModalita_menu', this).stop(true, true).delay(50).slideDown(100);
			        }, 
			        function () {
			            //nascondi sottomenu
			            $('#menuModalita').removeClass('tastoMenuHover');
			            $('#menuModalita_menu', this).stop(true, true).delay(150).slideUp(200);        
			        }
			    );
			
			$('#menuModalita_menu span[class*="item-icon"]').click(function() {
				var destinazione = $( this ).parent().children('span[class*="label"]').children().prop('href');
				if(destinazione) {
					// addTabID
					destinazione = addTabIdParam(destinazione);
					
					window.location = destinazione;
				}
			});
			
			$('#menuModalita_menu div[class*="menu-item"]').click(function() {
				var destinazione = $( this ).children('span[class*="label"]').children().prop('href');
				if(destinazione) {
					// addTabID
					destinazione = addTabIdParam(destinazione);
					
					window.location = destinazione;
				}
			});
			
			$('#menuModalita_menu').css('width', newItemWidth +'px');
		<% 	
	}
}%>

<% 
if(soggettoLinks!= null && soggettoLinks.size() > 0) {
	GeneralLink soggettoTitoloLink = soggettoLinks.get(0);
	if(soggettoLinks.size() == 1 && soggettoTitoloLink.getUrl().equals("")) {
		%>
		$('#menuSoggetto').css('cursor', 'default');
		<%
	}
	
	// visualizzo la tendina solo se ho piu' di due link (titolo + voci)
	if(soggettoLinks.size() > 1) { 
		
		int max = 0;
		// calcolo la lughezza massima della label
		for(int i = 1; i < soggettoLinks.size(); i++){
			if(soggettoLinks.get(i).getLabelWidth() > max)
				max = soggettoLinks.get(i).getLabelWidth();
		}
		%>	

			var newItemSoggettoWidth = <%=max %> + parseInt(paddLabelLeft) + parseInt(paddLabelRight) + parseInt(paddParentLabelLeft) + parseInt(paddParentLabelRight);
			
			// menu soggetto
			$('#menuSoggetto').hover(
			        function () {
			            //mostra sottomenu
			            // $('ul', this).stop(true, true).delay(50).slideDown(100); versione jquery > 1.3
			            //.animate({top: 0}, 50)
			            $('#menuSoggetto').addClass('tastoMenuHover');
			            $('#menuSoggetto_menu', this).stop(true, true).delay(50).slideDown(100);
			        }, 
			        function () {
			            //nascondi sottomenu
			            $('#menuSoggetto').removeClass('tastoMenuHover');
			            $('#menuSoggetto_menu', this).stop(true, true).delay(150).slideUp(200);        
			        }
			    );
			
			$('#menuSoggetto_menu span[class*="item-icon"]').click(function() {
				var destinazione = $( this ).parent().children('span[class*="label"]').children().prop('href');
				if(destinazione) {
					// addTabID
					destinazione = addTabIdParam(destinazione);
					
					window.location = destinazione;
				}
			});
			
			$('#menuSoggetto_menu div[class*="menu-item"]').click(function() {
				var destinazione = $( this ).children('span[class*="label"]').children().prop('href');
				if(destinazione) {
					// addTabID
					destinazione = addTabIdParam(destinazione);
					
					window.location = destinazione;
				}
			});
			
			$('#menuSoggetto_menu').css('width', newItemSoggettoWidth +'px');
		<% 
	}
} %>
	
	if(isIE()){
		if(IEVersione() == 10){
			$('#menuUtente_menu span[class*="icon-check"]').prop('data-useragent', navigator.userAgent);
		}
	}
});
</script>

<%
	}
%>
