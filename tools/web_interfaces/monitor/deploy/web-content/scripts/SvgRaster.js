function getImage(id) {
    var svg_style = '';
    var c3css = getSheet('c3.min.css');
    var cmcss = getSheet('ChartMap.css');
    var client = new HttpCSSCall();
    var params = { cmcss: cmcss, svg_style: svg_style, id: id };
    client.get(c3css.href, c3_response, error, params);
}

function getSheet(name) {
    var links = document.querySelectorAll('link');
    var sheet = false;
    var style;
    for (i = 0; i<links.length; i++) {
        if(links[i].href.indexOf(name) != -1) {
            sheet = true;
            style = links[i].sheet;
            break;
        }
    }
    if(!sheet) {
        error();
    }

    return style;
}


function c3_response(data, params) {
    params.svg_style = data.replace(/\r?\n|\r/g, '');
    var client = new HttpCSSCall();
    client.get(params.cmcss.href, cmcss_response, error, params);
}

function cmcss_response(data, params) {
    params.svg_style += data.replace(/\r?\n|\r/g, '');
    params.svg_style += 'line.c3-xgrid:not(:last-child){fill: none;stroke: #aaa;stroke-width: 1;stroke-dasharray: 3;}';
    params.svg_style += 'line.c3-xgrid:last-child{fill: none;stroke-width: 0;}';
    params.svg_style += 'line.c3-ygrid{fill: none;stroke: #aaa;stroke-width: 1;stroke-dasharray: 3;}.c3-axis ';
    params.svg_style += '.domain{fill: none;stroke: black;stroke-width: 1;}';
    params.svg_style = params.svg_style.replace(/\s+/g, ' ').split('}');

    checkSvgClass(params.svg_style, params.id);
}

function error() {
    console.warn('c3.min.css | ChartMap.css not loaded!');
}

function checkSvgClass(all_style, id) {
    var divC = document.getElementById(id);
    var size = divC.querySelector('svg').getBBox();
    var svg = divC.querySelector('svg').cloneNode(true);
    svg.style.background = 'white';
    svg.setAttribute('id', 'svg');
    var canvas = document.createElement('canvas');
    canvas.setAttribute('id', 'temporary_canvas');
    canvas.width  = svg.getAttribute('width');
    canvas.height = svg.getAttribute('height');
    var div = document.createElement('div');
    div.setAttribute('id', 'png');
    div.appendChild(svg);
    var group = document.createElement('div');
    group.setAttribute('id', 'temporary_group');
    group.style.display = 'none';
    group.appendChild(canvas);
    group.appendChild(div);
    divC.appendChild(group);

    all_style.forEach(function(el) {
        if (el.trim() != '') {
            var full_rule_string = el.split('{');
            var selector = full_rule_string[0].trim();
            var all_rule = full_rule_string[1].split(';');
            all_rule.forEach(function (elem) {
                if (elem.trim() != '') {
                    var attr_value = elem.split(':');
                    var prop = attr_value[0].trim();
                    var value = attr_value[1].trim();
                    d3.select('#'+svg.id).selectAll(selector).each(function(d, i){
                        if(!this.getAttribute(prop) && !this.style[prop]){
                            d3.select(this).style(prop + '', value + '');
                        }
                    });
                }
            });
        }
    });
    //Bar-Line
    d3.select('#'+svg.id).selectAll('.c3-chart-arc path').each(function(d, i){
        d3.select(this).style('stroke', '#fff');
    });
    //Pie
    d3.select('#'+svg.id).selectAll('.c3-chart-arc text').each(function(d, i){
        d3.select(this).style('font-size', '18px');
    });


    if (!HTMLCanvasElement.prototype.toBlob) {
        Object.defineProperty(HTMLCanvasElement.prototype, 'toBlob', {
            value: function (callback, type, quality) {

                var binStr = atob( this.toDataURL(type, quality).split(',')[1] ),
                    len = binStr.length,
                    arr = new Uint8Array(len);

                for (var i = 0; i < len; i++ ) {
                    arr[i] = binStr.charCodeAt(i);
                }

                callback( new Blob( [arr], {type: type || 'image/png'} ) );
            }
        });
    }

    var xml  = new XMLSerializer().serializeToString(svg);
    var encoded = (!window.btoa)?encode_btoa(xml):btoa(xml);
    var image = new Image;
    image.onerror = function() {
        console.log('Loading B64 Error. Try again...');
        this.alternative_encode_procedure(divC, xml);
    }.bind(this);
    image.onload = function(){
        var ctx = canvas.getContext("2d");
        var filename = nomeFileImageExport;
        this.generateFile(image, ctx, filename, canvas, group, divC);
    }.bind(this);
    image.src = "data:image/svg+xml;base64," + encoded;
}

function encode_btoa(input) {
    var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

    var str = String(input);
    for (
        var block, charCode, idx = 0, map = chars, output = '';
        str.charAt(idx | 0) || (map = '=', idx % 1);
        output += map.charAt(63 & block >> 8 - idx % 1 * 8)
    ) {
        charCode = str.charCodeAt(idx += 3/4);
        if (charCode > 0xFF) {
            throw new Error("Image characters outside of the Latin1 range.");
        }
        block = block << 8 | charCode;
    }
    return output;
}


function alternative_encode_procedure(divC, xml) {
    var canvas = divC.querySelector('#temporary_canvas');
    var group = divC.querySelector('#temporary_group');
    var a_image = new Image();
    a_image.onerror = function() {
        divC.removeChild(group);
        console.log('Data error.');
    }.bind(this);
    a_image.onload = function(){
        var ctx = canvas.getContext("2d");
        var filename = nomeFileImageExport;
        this.generateFile(a_image, ctx, filename, canvas, group, divC);
    }.bind(this);
    a_image.src = "data:image/svg+xml;base64," + this.alternative_b64_encode(xml);
}

function alternative_b64_encode(input) {
    var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var output = "";
    var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
    var i = 0;

    input = this._utf8_encode(input);
    while (i < input.length) {

        chr1 = input.charCodeAt(i++);
        chr2 = input.charCodeAt(i++);
        chr3 = input.charCodeAt(i++);
        enc1 = chr1 >> 2;
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
        enc4 = chr3 & 63;

        if (isNaN(chr2)) {
            enc3 = enc4 = 64;
        } else if (isNaN(chr3)) {
            enc4 = 64;
        }
        output = output + _keyStr.charAt(enc1) + _keyStr.charAt(enc2) + _keyStr.charAt(enc3) + _keyStr.charAt(enc4);
    }
    return output;

}

function _utf8_encode(string) {
    string = string.replace(/\r\n/g,"\n");
    var utftext = "";
    for (var n = 0; n < string.length; n++) {
        var c = string.charCodeAt(n);
        if (c < 128) {
            utftext += String.fromCharCode(c);
        }
        else if((c > 127) && (c < 2048)) {
            utftext += String.fromCharCode((c >> 6) | 192);
            utftext += String.fromCharCode((c & 63) | 128);
        }
        else {
            utftext += String.fromCharCode((c >> 12) | 224);
            utftext += String.fromCharCode(((c >> 6) & 63) | 128);
            utftext += String.fromCharCode((c & 63) | 128);
        }
    }

    return utftext;
}

function generateFile(image, ctx, filename, canvas, group, divC) {
    try {
        ctx.drawImage(image, 0, 0);
        canvas.toBlob(function (blob) {
            if (window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveOrOpenBlob(blob, filename);
                divC.removeChild(group);
                return;
            }
            //NON IE
            var a = document.createElement("a");
            a.download = filename;
            a.href = canvas.toDataURL("image/png");
            group.appendChild(a);
            a.click();
            divC.removeChild(group);
        });
    }
    catch (err)
    {
        //IE < Edge
        setTimeout(function() {
            divC.removeChild(group);
            var w = window.open("");
            if(w) {
            	w.document.write(image.outerHTML);
            } 
        } );
    }
}

var HttpCSSCall = function() {
    this.get = function(url, responseCallback, errorCallback, params) {
        params = params || {};
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4){
                if(xhr.status == 200) {
                    responseCallback(xhr.responseText, params);
                }
                else {
                    errorCallback(xhr.responseText, params);
                }
            }
        };

        xhr.open("GET", url, true);
        xhr.send(null);
    }
};
