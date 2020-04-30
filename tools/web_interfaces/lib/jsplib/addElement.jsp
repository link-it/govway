<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>

<html>
<%
String iddati = request.getParameter("iddati");
String params = (String) request.getAttribute("params");
String gdString = "GeneralData";
String pdString = "PageData";
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

if(params == null) params="";

GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);
String customListViewName = pd.getCustomListViewName();
%>

<head>
<meta charset="UTF-8">
<title><%= gd.getTitle() %></title>
<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="css/materialIcons/material-icons-fontface.css" type="text/css">
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<link rel="stylesheet" href="css/materialIcons.css" type="text/css">
<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-1.4.js"></script>
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<script type="text/javascript" src="js/webapps.js"></script>
<!--Funzioni di utilita -->
<script type="text/javascript">
var iddati = '<%= iddati %>';
var params = '<%= params %>';
var path = '<%= request.getContextPath()%>';
</script>
<script type="text/javascript" src="js/PostBack.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<style type="text/css">@import url(css/ui.datepicker.css);</style>
<script type="text/javascript" src="js/ui.datepicker.js"></script>
<script type="text/javascript" src="js/ui.datepicker-it.js"></script>
<script type="text/javascript" src="js/jquery.placement.below.js"></script>
<style type="text/css">@import url(css/time-picker.css);</style>
<script type="text/javascript" src="js/jquery.timepicker-table.js"></script>
<style type="text/css">@import url(css/ui.core.css);</style>
<style type="text/css">@import url(css/ui.theme.css);</style>
<style type="text/css">@import url(css/ui.dialog.css);</style>
<style type="text/css">@import url(css/ui.slider.css);</style>
<script type="text/javascript" src="js/ui.core.js"></script>
<script type="text/javascript" src="js/ui.dialog.js"></script>
<script type="text/javascript" src="js/ui.slider.js"></script>
<style type="text/css">@import url(css/bootstrap-tagsinput.css);</style>
<script type="text/javascript" src="js/jquery-on.js"></script>
<script type="text/javascript" src="js/jquery-promises.js"></script>
<script type="text/javascript" src="js/typeahead.bundle.js"></script>
<script type="text/javascript" src="js/bootstrap-tagsinput.js"></script>
<script type="text/javascript" src="js/jquery.searchabledropdown-1.0.8.min.js"></script>
<script>
var nr = 0;
function CheckDati() {
  if (nr != 0) {
    return false;
  }

  //I controlli si fanno direttamente nei .java
  nr = 1;
  document.form.submit();
};

</script>
<script type="text/javascript">
	function customInputNumberChangeEventHandler(e){
		
		if (e.target.value == '') {
	    	// do nothing
	    } else {
	    	if(e.target.min){
	    		if(parseInt(e.target.min) > parseInt(e.target.value)){
	    			e.target.value = e.target.min;
	    		}
	    	}
	    	
	    	if(e.target.max){
	    		if(parseInt(e.target.max) < parseInt(e.target.value)){
	    			e.target.value = e.target.max;
	    		}
	    	}
	    }
	}
	
	function mostraDataElementInfoModal(title,body){
		$("#dataElementInfoModal").prev().children('span').text(title);
		$("#dataElementInfoModalBody").html(body);
		$("#dataElementInfoModal").dialog("open");
	}

        $(document).ready(function(){

        	// info
        	if($(".spanIconInfoBox").length>0){
        		$(".spanIconInfoBox").click(function(){
        			var iconInfoBoxId = $(this).parent().attr('id');
        			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
        			console.log(idx);
        			if(idx) {
						var label = $("#hidden_title_iconInfo_"+ idx).val();
						var body = $("#hidden_body_iconInfo_"+ idx).val();
						mostraDataElementInfoModal(label,body);
        			}
    			});
        	}
        	
        	if($(".iconInfoBox-cb-info").length>0){
        		$(".iconInfoBox-cb-info").click(function(){
        			var iconInfoBoxId = $(this).attr('id');
        			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
        			console.log(idx);
        			if(idx) {
						var label = $("#hidden_title_iconInfo_"+ idx).val();
						var body = $("#hidden_body_iconInfo_"+ idx).val();
						mostraDataElementInfoModal(label,body);
        			}
    			});
        	}
                //date time tracciamento
                //date time diagnostica
                $(":input[name='datainizio']").datepicker({dateFormat: 'yy-mm-dd'});
                $(":input[name='datafine']").datepicker({dateFormat: 'yy-mm-dd'});

                showSlider($("select[name*='percentuale']:not([type=hidden])"));
                
             // Production steps of ECMA-262, Edition 6, 22.1.2.1
                if (!Array.from) {
                  Array.from = (function () {
                    var toStr = Object.prototype.toString;
                    var isCallable = function (fn) {
                      return typeof fn === 'function' || toStr.call(fn) === '[object Function]';
                    };
                    var toInteger = function (value) {
                      var number = Number(value);
                      if (isNaN(number)) { return 0; }
                      if (number === 0 || !isFinite(number)) { return number; }
                      return (number > 0 ? 1 : -1) * Math.floor(Math.abs(number));
                    };
                    var maxSafeInteger = Math.pow(2, 53) - 1;
                    var toLength = function (value) {
                      var len = toInteger(value);
                      return Math.min(Math.max(len, 0), maxSafeInteger);
                    };

                    // The length property of the from method is 1.
                    return function from(arrayLike/*, mapFn, thisArg */) {
                      // 1. Let C be the this value.
                      var C = this;

                      // 2. Let items be ToObject(arrayLike).
                      var items = Object(arrayLike);

                      // 3. ReturnIfAbrupt(items).
                      if (arrayLike == null) {
                        throw new TypeError('Array.from requires an array-like object - not null or undefined');
                      }

                      // 4. If mapfn is undefined, then let mapping be false.
                      var mapFn = arguments.length > 1 ? arguments[1] : void undefined;
                      var T;
                      if (typeof mapFn !== 'undefined') {
                        // 5. else
                        // 5. a If IsCallable(mapfn) is false, throw a TypeError exception.
                        if (!isCallable(mapFn)) {
                          throw new TypeError('Array.from: when provided, the second argument must be a function');
                        }

                        // 5. b. If thisArg was supplied, let T be thisArg; else let T be undefined.
                        if (arguments.length > 2) {
                          T = arguments[2];
                        }
                      }

                      // 10. Let lenValue be Get(items, "length").
                      // 11. Let len be ToLength(lenValue).
                      var len = toLength(items.length);

                      // 13. If IsConstructor(C) is true, then
                      // 13. a. Let A be the result of calling the [[Construct]] internal method 
                      // of C with an argument list containing the single item len.
                      // 14. a. Else, Let A be ArrayCreate(len).
                      var A = isCallable(C) ? Object(new C(len)) : new Array(len);

                      // 16. Let k be 0.
                      var k = 0;
                      // 17. Repeat, while k < len… (also steps a - h)
                      var kValue;
                      while (k < len) {
                        kValue = items[k];
                        if (mapFn) {
                          A[k] = typeof T === 'undefined' ? mapFn(kValue, k) : mapFn.call(T, kValue, k);
                        } else {
                          A[k] = kValue;
                        }
                        k += 1;
                      }
                      // 18. Let putStatus be Put(A, "length", len, true).
                      A.length = len;
                      // 20. Return A.
                      return A;
                    };
                  }());
                }
                
                var numInputs = Array.from(document.querySelectorAll('input[type=number]'));

               	for(var i = 0 ; i < numInputs.length; i++){
//                		if(numInputs[i].hasAttribute('gw-function')){
//                			numInputs[i].addEventListener('change', window[numInputs[i].getAttribute('gw-function')]);
//                		} else {
//                			numInputs[i].addEventListener('change', inputNumberChangeEventHandler	);
//                		}
               		
               		if(numInputs[i].hasAttribute('gw-function')){
               			$(numInputs[i]).on('change', window[numInputs[i].getAttribute('gw-function')]);
               		} else {
               			$(numInputs[i]).on('change', inputNumberChangeEventHandler	);
               		}
               	}
               	
               	function inputNumberChangeEventHandler(e){
               		if (e.target.value == '') {
               	    	if(e.target.min){
               	         e.target.value = e.target.min;
               	    	} else {
               	    		e.target.value = 0;
               	    	}
               	    } else {
               	    	if(e.target.min){
               	    		if(parseInt(e.target.min) > parseInt(e.target.value)){
               	    			e.target.value = e.target.min;
               	    		}
               	    	}
               	    	
               	    	if(e.target.max){
               	    		if(parseInt(e.target.max) < parseInt(e.target.value)){
               	    			e.target.value = e.target.max;
               	    		}
               	    	}
               	    }
               	}
                
                scrollToPostBackElement(destElement);
        });
</script>

<jsp:include page="/jsp/addElementCustom.jsp" flush="true" />
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
<table class="bodyWrapper">
	<tbody>
		<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
	
		<!-- TR3: Body -->
		<tr class="trPageBody">
			<jsp:include page="/jsplib/menu.jsp" flush="true" />
			<% if(customListViewName == null || "".equals(customListViewName)){ %>
				<jsp:include page="/jsplib/edit-page.jsp" flush="true" />
			<% } else {%>	
				<jsp:include page="/jsplib/edit-page-custom.jsp" flush="true" />
			<% } %>
		</tr>
	
		<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
	</tbody>
</table>
<div id="dataElementInfoModal" title="Info">
	<div id="dataElementInfoModalBody" class="contenutoModal"></div>
</div>
<jsp:include page="/jsplib/conferma.jsp" flush="true" />
</body>
</html>
