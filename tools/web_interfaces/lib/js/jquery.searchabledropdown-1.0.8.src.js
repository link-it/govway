/**
 * JQuery Searchable DropDown Plugin
 *
 * @required jQuery 1.3.x or above
 * @author Sascha Woo <xhaggi@users.sourceforge.net>
 * $Id: jquery.searchabledropdown.js 53 2012-11-22 08:48:14Z xhaggi $
 *
 * Copyright (c) 2012 xhaggi
 * https://sourceforge.net/projects/jsearchdropdown/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
(function($) {

	// register plugin
	var plugin = register("searchable");

    // defaults
	plugin.defaults = {
        maxListSize: 100,
        maxMultiMatch: 50,
        exactMatch: false,
        wildcards: true,
        ignoreCase: true,
        warnNoMatch: "--",
        latency: 200,
        zIndex: "auto",
        disableInput: false
    };

	/**
	 * Execute function
	 * element-specific code here
	 * @param {Options} settings Settings
	 */
	plugin.execute = function(settings, zindex) {

		var timer = null;
        var searchCache = null;
        var search = null;

		// do not attach on IE6 or lower
		if ($.browser.msie && parseInt(jQuery.browser.version) < 7)
			return this;

    	// only active select elements with drop down capability
        if (this.nodeName != "SELECT" || this.size > 1)
            return this;

        var self = $(this);
        var storage = {index: -1, options: null}; // holds data for restoring
        var idxAttr = "lang";
        var enabled = false;

        // detecting chrome
        $.browser.chrome = /chrome/.test(navigator.userAgent.toLowerCase());
        if($.browser.chrome) $.browser.safari = false;

        // lets you override the options
        // inside the dom objects class property
        // requires the jQuery metadata plugin
        // <div class="hello {color: 'red'}">ddd</div>
        if ($.meta){
            settings = $.extend({}, options, self.data());
        }

        // objects
        var wrapper = $("<div/>");
        wrapper.attr("id", this.id + "_wrapper");
        var overlay = $("<div/>");
        overlay.attr("id", this.id + "_overlay");
        var input = $("<input/>");
        input.attr("id", this.id + "_input");
        var selector = $("<div/>");
        selector.attr("id", this.id + "_selector");

        // matching option items
       // var noMatchItem = $("<div>"+settings.warnNoMatch+"</div>").attr("disabled", "true");

        var selectorHelper = {
	        /**
             * Return numero delle option presenti come div nella lista
             */
			optionLength: function() {
				var lg = $(selector).children(".combobox-item").length;
				//console.log('numero options filtrate' + lg);
				return lg;
	        },
	        /**
             * Return numero delle option presenti come div nella lista
             */
			options: function() {
				return $(selector).children(".combobox-item");
	        },
	        /**
	         * Returns the selected item of selector element
	         */
	        selected: function() {
	        	var sel = $(selector).children('.combobox-item-selected');
	        	if(sel && sel.length > 0) {
	        		//console.log('elemento selezionato 1: ' + sel.text());
	        		return sel;
	        	} 
	        	var selIdx = this.selectedIndex();
	        	sel = $(selector).children(".combobox-item").get(selIdx);
	        	//console.log('elemento selezionato 2: ' + sel.text());
        		return sel;
	        },
	        /**
	         * Get or Set the selectedIndex of the selector element
	         * @param {int} idx SelectedIndex
	         */
	        selectedIndex: function(idx) {
	        	if(idx > -1){
	        		var oldSelected = $($(selector).children('.combobox-item').get(selector.get(0).selectedIndex));
	        		//var oldSelected = $(selector).children(".combobox-item").children("input[id='divOpt_origIdx_"+selector.get(0).selectedIndex+"']").parent();
	        		oldSelected.removeClass('combobox-item-selected');
	        		
	        		//console.log('Idx nuovo: ' + idx);
	        		selector.get(0).selectedIndex = idx;
	        		
	        		var selParent = $($(selector).children('.combobox-item').get(selector.get(0).selectedIndex)); 
	        			//var selParent = $(selector).children(".combobox-item").children("input[id='divOpt_origIdx_"+idx+"']").parent();
	        		//console.log('elemento selezionato tmp: ' + selParent.text());
	        		selParent.addClass('combobox-item-selected');
	        		
	        		return selector.get(0).selectedIndex;
	        	}
	        	//console.log('Idx attuale: ' + selector.get(0).selectedIndex);
	        	return selector.get(0).selectedIndex;
	        },
	        /**
	         * Reset the entries, which can be choose to it's inital state
	         */
	        reset: function() {
	        	//console.log('Reset');

	        	var len = self.get(0).length;
	        	var idx = self.get(0).selectedIndex;
	        	
	        	// clear selector select element
	            selector.empty();
	            
	        	var cVal = '';
        	  	for (var i=0; i < len; i++) {
        	  		var divI = optionToDiv($(self.get(0).options[i]), i, i, cVal);
    				selector.append(divI);
    				divI.get(0).addEventListener("click", function(e) {
    					//console.log('BBB');
    				});
//    				$(document).on('click', '#' + divI.attr('id') , { idx : i }, divclick);
	            }
        	  	
        	  	 this.selectedIndex(idx);	      
	        }
        };

        // draw it
        draw();

        /*
         * EVENT HANDLING
         */
        var suspendBlur = false;
//        overlay.mouseover(function() {
//        	suspendBlur = true;
//        });
        overlay.mouseout(function() {
        	suspendBlur = false;
        });
        selector.mouseover(function() {
        	suspendBlur = true;
        });
        selector.mouseout(function() {
        	suspendBlur = false;
        });
        input.click(function(e) {
        	if(!enabled)
    			enable(e, true);
    		else
    			disable(e, false);
        });
        input.blur(function(e) {
        	disable(e, suspendBlur);
        });
        self.keydown(function(e) {
        	if(e.keyCode != 9 && !e.shiftKey && !e.ctrlKey && !e.altKey)
        		input.click();
        });
        self.click(function(e) {
        	selector.focus();
        });
        selector.click(function(e) {
            if (selectorHelper.selectedIndex() < 0)
            	return;
            disable(e, true);
        });
        selector.focus(function(e) {
        	input.focus();
        });
        selector.blur(function(e) {
        	if(!suspendBlur)
        		disable(e, true);
        });
        selector.mousemove(function(e) {
        	// modificato inserendo la gestione dell'hover sul singolo div
        });

        // toggle click event on overlay div
        overlay.click(function(e) {
        	if(!enabled)
    			enable(e, true);
    		else
    			disable(e, true);
        });

        // trigger event keyup
        input.keyup(function(e) {

        	// break searching while using navigation keys
        	if(jQuery.inArray(e.keyCode, new Array(9, 13, 16, 33, 34, 35, 36, 38, 40)) > -1)
        		return true;

        	// set search text
        	search = $.trim(input.val().toLowerCase());

        	// if a previous timer is running, stop it
        	clearSearchTimer();

            // start new timer
            timer = setTimeout(searching, settings.latency);
        });

        // trigger keydown event for keyboard usage
        input.keydown(function(e) {

        	// tab stop
        	if(e.keyCode == 9) {
        		disable(e, true);
        	}

        	// return on shift, ctrl, alt key mode
        	if(e.shiftKey || e.ctrlKey || e.altKey)
        		return;

        	// which key is pressed
            switch(e.keyCode) {
                case 13:  // enter
                	disable(e, true);
                	self.focus();
                    break;
                case 27: // escape
                	disable(e);
					self.focus();
                	break;
                case 33: // pgup -8
                    if ((selectorHelper.selectedIndex() - 8) > 0) {
                        selectorHelper.selectedIndex(selectorHelper.selectedIndex() - 8);
                    }
                    else {
					    selectorHelper.selectedIndex(0);
				    }
                    synchronize();
                    break;
                case 34: // pgdown +8
                    if ((selectorHelper.selectedIndex() + 8) < (selectorHelper.optionLength() - 1)) {
                        selectorHelper.selectedIndex(selectorHelper.selectedIndex() + 8);
                    }
                    else {
					    selectorHelper.selectedIndex(selectorHelper.optionLength() -1);
				    }
                    synchronize();
                    break;
                case 38: // up
                    if (selectorHelper.selectedIndex() > 0){
                        selectorHelper.selectedIndex(selectorHelper.selectedIndex()-1);
                        synchronize();
                    }
                    break;
                case 40: // down
                    if (selectorHelper.selectedIndex() < selectorHelper.optionLength() - 1){
                    	selectorHelper.selectedIndex(selectorHelper.selectedIndex()+1);
                        synchronize();
                    }
                    break;
                default:
                    return true;
            }

            // we handled the key.stop
            // doing anything with it!
            return false;
        });

        /**
         * Draw the needed elements
         */
        function draw() {

            // fix some styles
            self.css("text-decoration", "none");
            self.width(self.outerWidth());
            self.height(self.outerHeight());

            // wrapper styles
            wrapper.css("position", "relative");
            wrapper.css("width", self.outerWidth());
            wrapper.css("margin-left", "0px");
            wrapper.css("display", "inline-flex");
            
            // relative div needs an z-index (related to IE z-index bug)
            if($.browser.msie)
            	wrapper.css("z-index", zindex);

            // overlay div to block events of source select element
            overlay.css({
                "position": "absolute",
                "top": 0,
                "left": 0,
                "width":  self.outerWidth() - 1,
                "height": self.outerHeight(),
                "background-color": "transparent"
            });

            // overlay text field for searching capability
            input.attr("type", "text");
            input.hide();
            input.height(self.outerHeight() - 1);

            // default styles for text field
            input.addClass('inputLinkLong');
            input.css({
                "position": "absolute",
                "top": 0,
                "left": 0,
                "margin": "1px",
                "padding": "0px",
                "outline-style": "none",
                "border-style": "solid",
            	"border-bottom-style": "none",
        		"border-color": "transparent"
            });

            // copy selected styles to text field
    		var sty = new Array();
    		sty.push("border-left-width");
    		sty.push("border-top-width");
    		//sty.push("font-family");
    		sty.push("font-size");
    		sty.push("font-stretch");
    		sty.push("font-variant");
    		sty.push("font-weight");
    		sty.push("color");
    		sty.push("text-align");
    		sty.push("text-indent");
    		sty.push("text-shadow");
    		sty.push("text-transform");
    		sty.push("padding-left");
    		sty.push("padding-top");
    		for(var i=0; i < sty.length;i++)
    			input.css(sty[i], self.css(sty[i]));

    		// adjust search text field
    		// IE7
    		if($.browser.msie && parseInt(jQuery.browser.version) < 8) {
    			input.css("padding", "0px");
    			input.css("padding-left", "3px");
    			input.css("border-left-width", "2px");
    			input.css("border-top-width", "3px");
    		}
    		// chrome
    		else if($.browser.chrome) {
    			input.height(self.innerHeight());
    			input.css("text-transform", "none");
    			input.css("padding-left", parseFloatPx(input.css("padding-left"))+3);
    			input.css("padding-top", 0);
    		}
    		// safari
    		else if($.browser.safari) {
    			input.height(self.innerHeight());
    			input.css("padding-top", 2);
    			input.css("padding-left", 3);
    			input.css("text-transform", "none");
    		}
    		// opera
    		else if($.browser.opera) {
    			input.height(self.innerHeight());
    			var pl = parseFloatPx(self.css("padding-left"));
    			input.css("padding-left", pl == 1 ? pl+1 : pl);
    			input.css("padding-top", 0);
    		}
    		else if($.browser.mozilla) {
    			input.css("padding-top", "0px");
//    			input.css("border-top", "0px");
    			input.css("padding-left", parseFloatPx(self.css("padding-left"))+3);
    		}
    		// all other browsers
    		else {
    			input.css("padding-left", parseFloatPx(self.css("padding-left"))+3);
    			input.css("padding-top", parseFloatPx(self.css("padding-top"))+1);
    		}

    		// adjust width of search field
    		var offset = parseFloatPx(self.css("padding-left")) + parseFloatPx(self.css("padding-right")) +
    		parseFloatPx(self.css("border-left-width")) + parseFloatPx(self.css("border-left-width")) + 16;
            input.width(self.outerWidth() - offset);

    		// store css width of source select object then set width
    		// to auto to obtain the maximum width depends on the longest entry.
    		// this is nessesary to set the width of the selector, because min-width
    		// do not work in all browser.
            var w = self.css("width");
            var ow = self.outerWidth();
            self.css("width", "auto");
            ow = ow > self.outerWidth() ? ow : self.outerWidth();
            self.css("width", w);

            // entries selector replacement
            selector.hide();
            selector.addClass('combobox-list');
            selector.css({
                "top": self.outerHeight(),
                "left": 0,
                "width": ow
            });

            // z-index handling
            var zIndex = /^\d+$/.test(self.css("z-index")) ? self.css("z-index") : 1;
            // if z-index option is defined, use it instead of select element z-index
            if (settings.zIndex && /^\d+$/.test(settings.zIndex))
            	zIndex = settings.zIndex;
            overlay.css("z-index", (zIndex).toString(10));
            input.css("z-index", (zIndex+1).toString(10));
            selector.css("z-index", (zIndex+2).toString(10));

            // append to container
            self.wrap(wrapper);
            self.after(overlay);
            self.after(input);
            self.after(selector);
        };

        /**
         * Enable the search facilities
         *
         * @param {Object} e Event
		 * @param {boolean} s Show selector
         * @param {boolean} v Verbose enabling
         */
        function enable(e, s, v) {

    		// exit event on disabled select element
    		if(self.attr("disabled"))
    			return false;

    		// set state to enabled
    		if(typeof v == "undefined")
    			enabled = !enabled;

    		// reset selector
    		selectorHelper.reset();

    		// synchronize select and dropdown replacement
    		synchronize();

    		overlay.addClass('combobox-overlay-focus');
        	// show selector
        	if(s)
        		selector.show();

    		// show search field
        	if(!settings.disableInput){
        		input.show();
        		input.focus();
        		input.select();
        	}
        	
        	if(typeof e != "undefined")
        		e.stopPropagation();
        };

        /**
         * Disable the search facilities
         *
         * @param {Object} e Event
		 * @param {boolean} rs Restore last results
         */
        function disable(e, populateChanges) {

        	// set state to disabled
        	enabled = false;

            // clear running search timer
        	clearSearchTimer();

			// hide search field and selector
			input.hide();
        	selector.hide();
        	overlay.removeClass('combobox-overlay-focus');

			// populate changes
        	if(populateChanges)
        		populate();

        	console.log('check:' + e.target);
        	
            if(typeof e != "undefined")
            	e.stopPropagation();
        };
        
        /**
         * Clears running search timer
         */
        function clearSearchTimer() {
        	// clear running timer
            if(timer != null)
            	clearTimeout(timer);
        };

        /**
         * Populate changes to select element
         */
        function populate() {
        	//console.log('populate: ' + selectorHelper.selectedIndex());
        	if(selectorHelper.selectedIndex() < 0)
        		return;

        	// store selectedIndex
        	// estrazione del idx corrispondente per la select
        	var val = $(selectorHelper.selected()).children("input[id*='divOpt_origIdx_']").val();
        	//console.log('populate idx select originale: ' + val);
        	if(self.get(0).selectedIndex != val) {
        		self.get(0).selectedIndex = val;

        		// 	trigger change event
        		self.change();
        	}
        };

        /**
         * Synchronize selected item on dropdown replacement with source select element
         */
        function synchronize() {
        	//console.log('synch: ' + selectorHelper.selectedIndex());
        	if(selectorHelper.selectedIndex() > -1){
        		input.val(selector.find(".combobox-item-selected").text());
        	}else
        		input.val(self.find(":selected").text());
        };

        /**
         * Crea il div a partire dalla option
         */
        function optionToDiv(option, i, origIdx, searchVal) {
        	var text = option.text();
        	if(searchVal && searchVal.length > 0) {
        		text = createHighlightItem(option.text(), searchVal);
        	}
        	var attrVal = option.val();
        	var attrSel = option.attr('selected');
        	var div = $("<div>"+text+"</div>");
        	div.attr("id", "divOpt_" + i);
        	div.addClass('combobox-item');
        	
        	var inputValue = $("<input/>");
        	inputValue.attr("type", "hidden");
        	inputValue.attr("value", attrVal);
        	inputValue.attr("id", "divOpt_value_" + i);
        	div.append(inputValue);
        	
        	var inputOrigLabel = $("<input/>");
        	inputOrigLabel.attr("type", "hidden");
        	inputOrigLabel.attr("value", option.text());
        	inputOrigLabel.attr("id", "divOpt_origLabel_" + i);
        	div.append(inputOrigLabel);
        	
        	// salvo il vecchio indice
        	var inputOrigIdx = $("<input/>");
        	inputOrigIdx.attr("type", "hidden");
        	inputOrigIdx.attr("value", origIdx);
        	inputOrigIdx.attr("id", "divOpt_origIdx_" + i);
        	div.append(inputOrigIdx);
        	
        	if(attrSel) {
        		div.addClass('combobox-item-selected');
        	}
        	
        	// highlight della scelta su cui passo il mouse
        	div.hover(hoverOn,hoverOff); 
        	
        	return div;
        };
        
        function divclick(e) {
        	console.log('CLICK');
        	console.log(e.data);
        	console.log(e.target);
        };
        
        function calcolaIdx(idValue) {
        	var pos = idValue.indexOf("_");
        	return parseInt(idValue.substr(pos + 1));
        };
        
        function hoverOn(e) {
        	var val = calcolaIdx($(this).attr('id'));
        	//console.log('Hover:' + val);
        	selectorHelper.selectedIndex(val);
        };
        
        function hoverOff(e) {
        	$(this).removeClass('combobox-item-selected');
        };
        
        /**
         * Escape regular expression string
         *
         * @param str String
         * @return escaped regexp string
         */
        function escapeRegExp(str) {
        	var specials = ["/", ".", "*", "+", "?", "|", "(", ")", "[", "]", "{", "}", "\\", "^", "$"];
        	var regexp = new RegExp("(\\" + specials.join("|\\") + ")", "g");
        	return str.replace(regexp, "\\$1");
    	};

        /**
         * The actual searching gets done here
         */
        function searching() {
            if (searchCache == search) { // no change ...
                timer = null;
                return;
            }

            var matches = 0;
            searchCache = search;
            selector.hide();
            selector.empty();

            // escape regexp characters
            var regexp = escapeRegExp(search);
            // exact match
            if(settings.exactMatch)
            	regexp = "^" + regexp;
            // wildcard support
            if(settings.wildcards) {
            	regexp = regexp.replace(/\\\*/g, ".*");
            	regexp = regexp.replace(/\\\?/g, ".");
            }
            // ignore case sensitive
            var flags = null;
            if(settings.ignoreCase)
            	flags = "i";

            // RegExp object
            search = new RegExp(regexp, flags);

			// for each item in list 
            // && matches < settings.maxMultiMatch
            for(var i=0; i < self.get(0).length; i++){
            	// search
                if(search.length == 0 || search.test(self.get(0).options[i].text)){
                	var divI = optionToDiv($(self.get(0).options[i]), matches , i, searchCache);
    				selector.append(divI);
    				divI.get(0).addEventListener("click", function(e) {
    				//	console.log('BBB');
    				});
//    				$(document).on('click', '#' +  divI.attr('id') , { idx : i }, divclick);
                    matches++;
                }
            }

            // result actions
            if(matches >= 1){
                selectorHelper.selectedIndex(0);
            }
            // allineo il comportamento degli altri componenti se la ricerca non da risultati visualizzo il div vuoto
//            else if(matches == 0){	
               // selector.append(noMatchItem);
//            }

            // resize selector
            selector.show();
            timer = null;
        };

        /**
         * Parse a given pixel size value to a float value
         * @param value Pixel size value
         */
        function parseFloatPx(value) {
			try {
				value = parseFloat(value.replace(/[\s]*px/, ""));
				if(!isNaN(value))
					return value;
			}
			catch(e) {}
			return 0;
		};

        return;
    };

    /**
     * Register plugin under given namespace
     *
     * Plugin Pattern informations
     * The function creates the namespace under jQuery
     * and bind the function to execute the plugin code.
     * The plugin code goes to the plugin.execute function.
     * The defaults can setup under plugin.defaults.
     *
     * @param {String} nsp Namespace for the plugin
     * @return {Object} Plugin object
     */
    function register(nsp) {

    	// init plugin namespace
    	var plugin = $[nsp] = {};

    	// bind function to jQuery fn object
    	$.fn[nsp] = function(settings) {
    		// extend default settings
    		settings = $.extend({}, plugin.defaults, settings);
//    		settings = $.extend(plugin.defaults, settings);

    		var elmSize = this.size();
            return this.each(function(index) {
            	plugin.execute.call(this, settings, elmSize-index);
            });
        };

        return plugin;
	};

})(jQuery);
