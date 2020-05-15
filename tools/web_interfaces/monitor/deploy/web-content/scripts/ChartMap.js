function createChart(chartId, dataString, type, w, h, barwidth) {

    if(dataString.length == 0 || !type) {
        return;
    }

    var chart = document.getElementById(chartId);
    while (chart.firstChild) {
        chart.removeChild(chart.firstChild);
    }

    var dataJson = JSON.parse(dataString);
    var size = { 'w': w, 'h': h };
    generateChart(chartId, dataJson, type, size, barwidth);
}

/**
 * Creazione grafico
 * @param id
 * @param _dataJson
 * @param _type
 * @param _size
 * @param _barwidth
 */
function generateChart(id, _dataJson, _type, _size, _barwidth) {
    this.setPolyfill();

    var dp = chartMapping(_dataJson, _type, _size);

    var isInset = false;
    if(dp.noData != 0) {
        //Multi columns legend
        isInset = (dp.rows[0].length>dp.limiteColonneLegenda);
    }
    var options = {
        bindto: '#'+id,
        size: {
            width: dp.size.w,
            height: dp.size.h
        },
        legend: {
            itemPerColumn: isInset?dp.colonneLegenda:0,
            clickItemLegenda: dp.clickItemLegenda,
            position: 'right',
            show: dp.showLegend
        },
        grid: {
            x: {
                show: true
            },
            y: {
                show: true
            }
        },
        padding: {
            top: (dp.type=='pie')?75:85,
            bottom: (dp.type=='pie')?0:40
        },
        data: {
            rows: dp.rows,
            names: dp.labelRef,
            type: dp.type
        },
        color: {
            pattern: dp.colors
        },
        axis: {
            x: {
                type: 'category',
                categories: dp.cats,
                tick: {
                    rotate: dp.rotation,
                    multiline: false
                },
                height: dp.axisHeight,
                fullGrid: dp.xAxisGridLine
            },
            y: {
                label: {
                    text: dp.yLabel,
                    position: 'outer-middle'
                },
                min: (dp.type=='line')?0:undefined,
                padding: (dp.type=='line')?{ bottom: 0 }:undefined
            }
        },
        bar: {
            zerobased: true,
            width: {
                ratio: (_barwidth || .6)
            }
        },
        tooltip: {
            show: dp.tips.length != 0,
            contents: function (d, defaultTitleFormat, defaultValueFormat, color) {
                var $$ = this, config = $$.config,
                    titleFormat = config.tooltip_format_title || defaultTitleFormat,
                    nameFormat = config.tooltip_format_name || function (name) {
                            return name;
                        },
                    valueFormat = config.tooltip_format_value || defaultValueFormat,
                    text, i, title, value, name, bgcolor;
                for (i = 0; i < d.length; i++) {
                    if (!(d[i] && (d[i].value || d[i].value === 0))) {
                        continue;
                    }

                    if (!text) {
                        var cat = (c3.extra.limit !== 0)?c3.extra.catsTooltip[d[i].x]:c3.extra.categories[d[i].x];
                        title = titleFormat ? cat : d[i].x;
                        //title = titleFormat ? titleFormat(d[i].x) : d[i].x;
                        text = "<table class='" + $$.CLASS.tooltip + "' style='word-wrap: break-word !important; max-width: 250px;'>" + (title || title === 0 ? "<tr><th colspan='2'>" + title + "</th></tr>" : "");
                    }

                    name = nameFormat(d[i].name);
                    if (c3.extra.limitLegenda !== -1) {
                        if(this.config.data_type !== 'pie') {
                            name = nameFormat(c3.extra.legendTooltip[d[i].id]);
                        }
                        if(this.config.data_type === 'pie') {
                            name = nameFormat(c3.extra.legendTooltip[d[i].index]);
                        }
                    }
                    if(this.config.data_type === 'pie') {
                        //value = valueFormat(d[i].value, d[i].ratio, d[i].id, d[i].index);
                        value = c3.extra.tips[i][d[i].index];
                    }
                    else value = c3.extra.tips[d[i].index][i];
                    bgcolor = $$.levelColor ? $$.levelColor(d[i].value) : color(d[i].id);

                    text += "<tr class='" + $$.CLASS.tooltipName + "-" + d[i].id + "'>";
                    text += "<td class='name'><span style='background-color:" + bgcolor + "'></span>" + name + "</td>";
                    text += "<td class='value'>" + value + "</td>";
                    text += "</tr>";
                }
                return text + "</table>";
            }
        },
        oninit: function () {
            if(this.config.data_type == 'pie') {
                this.config.resize_auto = false;
            }
        },
        onresize: function() {
            if(this.config.size_width != undefined) {
                this.config.size_width = undefined;
            }
            /*if(this.config.size_height != undefined && this.config.data_type != 'pie') {
                this.config.size_height = undefined;
            }*/
            if(this.config.data_type == 'pie') {
                this.api.flush();
            }
        },
        onrendered: function() {
            var _svg = this.svg.node();
            var _svg_width = parseFloat(_svg.getAttribute('width'));
            var _title = _svg.querySelector('#title');
            var _subtitle = _svg.querySelector('#subtitle');
            var _warning = _svg.querySelector('#warning');

            if(_title && _title.innerHTML) {
                if(_title.getComputedTextLength() > (_svg_width - 15)) {
                    _title.innerHTML = _title.innerHTML.slice(0, -(_title.innerHTML.length/3))+'...';
                }
                else if((_svg_width - 15) >= c3.extra.title_ellipsis && _title.innerHTML.indexOf('...') != -1){
                    _title.innerHTML = c3.extra.title;
                }
                var _newXTitle = (_svg_width - _title.getBBox().width)/2;
                var _txTitle = _newXTitle - _title.getBBox().x;
                _title.setAttribute('transform', 'translate('+_txTitle+')');
            }
            if(_subtitle && _subtitle.innerHTML) {
                if(_subtitle.getComputedTextLength() > (_svg_width - 15)) {
                    _subtitle.innerHTML = _subtitle.innerHTML.slice(0, -(_subtitle.innerHTML.length/3))+'...';
                }
                else if((_svg_width - 15) >= c3.extra.subtitle_ellipsis && _subtitle.innerHTML.indexOf('...') != -1){
                    _subtitle.innerHTML = c3.extra.subtitle;
                }
                var _newXSubtitle = (_svg_width - _subtitle.getBBox().width)/2;
                var _txSubtitle = _newXSubtitle - _subtitle.getBBox().x;
                _subtitle.setAttribute('transform', 'translate('+_txSubtitle+')');
            }
            //noData
            if(_warning && _warning.innerHTML) {
                var _grid_size = _svg.querySelector('.c3-axis.c3-axis-y').getBBox();
                if(_warning.getComputedTextLength() > (_svg_width - 15 - _grid_size.width)) {
                    _warning.innerHTML = _warning.innerHTML.slice(0, -(_warning.innerHTML.length/2))+'...';
                }
                else if((_svg_width - 15 - _grid_size.width) >= c3.extra.warning_ellipsis && _warning.innerHTML.indexOf('...') != -1){
                    _warning.innerHTML = c3.extra.warning;
                }
                var _newXWarning = (_svg_width - _warning.getBBox().width)/2;
                var _txWarning = _grid_size.width/2 + (_newXWarning - _warning.getBBox().x);
                _warning.setAttribute('transform', 'translate('+_txWarning+')');
            }
        }
    };

    if(dp.valoreRealeTorta) {
        options.pie = { label: { format: function (value, ratio, id) { return d3.format('.1f')(value*100/c3.extra.pieTotal)+'%'; } } };
    }

    if(dp.type == 'pie') {
        delete options.axis;
        delete options.bar;
        delete options.grid;
    } else {
        if(options.hasOwnProperty('pie')) {
            delete options.pie;
        }
    }

    c3.extra = { tips: dp.tips,
                title: dp.title,
             subtitle: dp.subtitle,
       title_ellipsis: 0,
    subtitle_ellipsis: 0,
              warning: dp.noDataLabel,
     warning_ellipsis: 0,
        axis_tooltips: false,
  ellipsis_textLength: 0,
        ellipsis_rect: [],
           categories: [].concat(dp.cats),
             pieTotal: dp.pieTotal,
        legendTooltip: dp.legendTooltip,
          catsTooltip: dp.catsTooltip,
         limitLegenda: dp.limitLegenda,
                limit: dp.limit,
                 type: dp.type
    };

    var chart = c3.generate(options);

    var d = document.getElementById(id);
    var svg = d.querySelector('svg');
    var _style = svg.getAttribute('style');
    if(_style.indexOf('Roboto;') == -1){
        svg.style.fontFamily =  'Roboto';
    }

    if(dp.type != 'pie' && dp.rotation != 0 && dp.noData != 0) {
        var _max_cat = document.createElementNS("http://www.w3.org/2000/svg", "text");
        _max_cat.setAttributeNS(null,"opacity", 0);
        _max_cat.appendChild(document.createTextNode(dp.maxCategory));
        svg.appendChild(_max_cat);
        var _firstTick = svg.querySelector('.c3-event-rects rect').getBBox().width;
        var _firstText = svg.querySelector('.c3-axis.c3-axis-x text tspan').getBoundingClientRect().width;
        var _yAxisWidth = svg.querySelector('.c3-axis.c3-axis-y').getBBox().width;
        var _pad = _firstText - (_firstTick/2);

        if(_pad > _yAxisWidth){
            options.padding.left = _pad + _max_cat.getBBox().height;
        }
        options.axis.x.height = Math.abs(_max_cat.getComputedTextLength()*Math.sin(dp.rotation/180*Math.PI));
        options.size.height = dp.size.h + options.axis.x.height;
        svg.removeChild(_max_cat);

        chart = c3.generate(options);
        svg = d.querySelector('svg');
    }

    var title = document.createElementNS("http://www.w3.org/2000/svg", "text");
    title.setAttributeNS(null,"x",0);
    title.setAttributeNS(null,"y",20);
    title.setAttributeNS(null,"id","title");
    title.setAttributeNS(null,"width", dp.size.w+'px');
    title.setAttributeNS(null,"class","c3-chart-title");

    title.appendChild(document.createTextNode(dp.title));

    svg.appendChild(title);
    var s = title.getBBox();
    title.setAttributeNS(null,"x", (dp.size.w-s.width)/2);

    var subtitle = document.createElementNS("http://www.w3.org/2000/svg", "text");
    subtitle.setAttributeNS(null,"x",0);
    subtitle.setAttributeNS(null,"y",55);
    subtitle.setAttributeNS(null,"id","subtitle");
    subtitle.setAttributeNS(null,"width", dp.size.w+'px');
    subtitle.setAttributeNS(null,"class","c3-chart-subtitle");

    subtitle.appendChild(document.createTextNode(dp.subtitle));

    svg.appendChild(title);
    svg.appendChild(subtitle);
    var s = title.getBBox();
    var st = subtitle.getBBox();
    title.setAttributeNS(null,"x", (dp.size.w-s.width)/2);
    subtitle.setAttributeNS(null,"x", (dp.size.w-st.width)/2);
    c3.extra.title_ellipsis = title.getBBox().width;
    c3.extra.subtitle_ellipsis = subtitle.getBBox().width;

    //No data
    if(dp.noData == 0) {
        var warning = document.createElementNS("http://www.w3.org/2000/svg", "text");
        warning.setAttributeNS(null,"x",0);
        warning.setAttributeNS(null,"y", dp.size.h/2);
        warning.setAttributeNS(null,"id","warning");
        warning.setAttributeNS(null,"width", dp.size.w+'px');
        warning.setAttributeNS(null,"class","c3-chart-nodata");

        warning.appendChild(document.createTextNode(dp.noDataLabel));
        svg.appendChild(warning);
        var wrn = warning.getBBox();
        warning.setAttributeNS(null,"x", (dp.size.w-wrn.width)/2);
        c3.extra.warning_ellipsis = warning.getBBox().width;
    }

    //x-Axis: Tooltip categories
    /* fa sbarellare il browser quando vi sono informazioni lunghe
       Inoltre l'informazione e' gia presente nel tooltip della barra
    if(dp.type != 'pie' && dp.noData != 0) {
        var _ellipsis = document.createElementNS("http://www.w3.org/2000/svg", "text");
        _ellipsis.setAttributeNS(null,"opacity", 0);
        _ellipsis.appendChild(document.createTextNode('...'));
        svg.appendChild(_ellipsis);
        c3.extra.ellipsis_textLength = 2*parseInt(_ellipsis.getComputedTextLength());
        svg.removeChild(_ellipsis);

        d3.select("body").append("div").attr("class", "tooltip-category-div").style("opacity", 0)
            .style("position", "absolute").style("background-color", "#fff").style("padding", "5px")
            .style("border", "1px solid #ccc");
        var axis = svg.querySelector('g.c3-axis.c3-axis-x');
        axis.querySelectorAll(".tick text").forEach(function(d1) {
            var data = { text: d3.select(d1).node().textContent, x: d3.select(d1).node().getBBox().width };
            d3.select(d1).on("mouseover", function(d) {
                if(d3.select(d1).node().textContent.indexOf('...')!=-1){
                    var _div = d3.select(".tooltip-category-div");
                    _div.transition().duration(100).style("opacity", 1);
                    var ttip = c3.extra.catsTooltip[d];
                    if(data.x < dp.size.w - d3.event.pageX) {
                        _div.html(ttip).style("word-wrap", "break-word").style("left", d3.event.pageX + "px").style("top", (d3.event.pageY - 56) + "px");
                    } else {
                        _div.html(ttip).style("word-wrap", "break-word").style("left", Math.abs(d3.event.pageX - data.x) + "px").style("top", (d3.event.pageY - 56) + "px");
                    }
                }
            }).on("mouseout", function(d) {
                var _div = d3.select(".tooltip-category-div");
                _div.style("opacity", 0).style("left", '0px').style("top", "0px");
            });
        });
        c3.extra.axis_tooltips = true;
        chart.flush();
    }
    */

    d3.selectAll('g.c3-legend-item')
        .on('mouseover', function (id, index) {
            chart.focus(id);
            var _text = (c3.extra.type !== 'pie')?c3.extra.legendTooltip[id]:c3.extra.legendTooltip[index];
            if(c3.extra.limitLegenda !== -1) {
                var size = d3.select('svg').node().getBoundingClientRect();
                var target = d3.select(this).node().getBoundingClientRect();
                d3.select(".c3-tooltip-container")
                    .html("")
                    .style("top", (size.top + window.scrollY + 10) +"px")
                    .style("left", Math.abs(.5*size.width-2*target.width) + "px")
                    .style("display", "block")
                    .append("div")
                    .style("width", 4*target.width+"px")
                    .style("word-wrap", "break-word")
                    .style("padding", "8px")
                    .style("opacity", ".9")
                    .style("border", "1px solid #eee")
                    .style("box-shadow", "7px 7px 12px -9px #777")
                    .style("background-color", "#fff")
                    .html("<span>"+_text+"</span>");
            }
        })
        .on('mouseout', function (id) {
            chart.revert();
            if(c3.extra.limitLegenda !== -1) {
                d3.select(".c3-tooltip-container").html("");
            }
        });

}

function hoverLegend (id, index) {
    chart.focus(id);
    var _text = (c3.extra.type !== 'pie')?c3.extra.legendTooltip[id]:c3.extra.legendTooltip[index];
    if(c3.extra.limitLegenda !== -1) {
        var size = d3.select('svg').node().getBoundingClientRect();
        var target = d3.select(this).node().getBoundingClientRect();
        d3.select(".c3-tooltip-container")
            .html("")
            .style("top", (size.top + window.scrollY + 10) +"px")
            .style("left", Math.abs(.5*size.width-2*target.width) + "px")
            .style("display", "block")
            .append("div")
            .style("width", 4*target.width+"px")
            .style("word-wrap", "break-word")
            .style("padding", "8px")
            .style("opacity", ".9")
            .style("border", "1px solid #eee")
            .style("box-shadow", "7px 7px 12px -9px #777")
            .style("background-color", "#fff")
                .html("<span>"+_text+"</span>");
    }
}

/**
 * Mappatura grafico
 * @param _dataJson
 * @param _type
 * @param size
 */
function chartMapping(_dataJson, _type, _size) {

    var dpChart = {};
    dpChart.title = _dataJson.hasOwnProperty('titolo')?labelUnescape(_dataJson.titolo):'';
    dpChart.subtitle = _dataJson.hasOwnProperty('sottotitolo')?labelUnescape(_dataJson.sottotitolo):'';
    dpChart.yLabel = _dataJson.hasOwnProperty('yAxisLabel')?labelUnescape(_dataJson.yAxisLabel):'';
    dpChart.showLegend = _dataJson.hasOwnProperty('mostraLegenda')?_dataJson.mostraLegenda:true;
    dpChart.limit = _dataJson.hasOwnProperty('limit')?parseInt(_dataJson.limit,10):-1;
    dpChart.limitLegenda = _dataJson.hasOwnProperty('limitLegenda')?parseInt(_dataJson.limitLegenda,10):-1;
    dpChart.valueOnLegend = _dataJson.hasOwnProperty('valueOnLegend')?_dataJson.valueOnLegend:false;
    dpChart.rotation = _dataJson.hasOwnProperty('xAxisLabelDirezione')?Math.abs(_dataJson.xAxisLabelDirezione):0;
    dpChart.rotation = dpChart.rotation != 0?-dpChart.rotation:0;
    dpChart.xAxisGridLine = _dataJson.hasOwnProperty('xAxisGridLine')?_dataJson.xAxisGridLine:true;
    dpChart.axisHeight = 30;
    dpChart.maxCategory = '';
    dpChart.colonneLegenda = _dataJson.hasOwnProperty('colonneLegenda')?_dataJson.colonneLegenda:8;
    dpChart.limiteColonneLegenda = _dataJson.hasOwnProperty('limiteColonneLegenda')?_dataJson.limiteColonneLegenda:8;
    dpChart.clickItemLegenda = _dataJson.hasOwnProperty('clickItemLegenda')?_dataJson.clickItemLegenda:true;
    dpChart.valoreRealeTorta = _dataJson.hasOwnProperty('valoreRealeTorta')?_dataJson.valoreRealeTorta:false;
    dpChart.pieTotal = 0;

    var serieRef = [];
    var serie = [];
    var serieColors = [];
    var labelRef = {};
    var cats = [];
    var tips = [];
    var noData = -1;
    var noDataLabel = '';


    noData = _dataJson.hasOwnProperty("noData")?0:-1;
    noDataLabel = (noData == -1)?'':labelUnescape(_dataJson.noData);

    if(_type != 'pie') {
        if(_dataJson.hasOwnProperty("categorie") && _dataJson.hasOwnProperty("dati") &&
            _dataJson.hasOwnProperty("coloriAutomatici")) {
            serieRef = _dataJson.categorie.map(function (item) {
                return item.key;
            });
            if (!_dataJson.coloriAutomatici) {
                serieColors = _dataJson.categorie.map(function (item) {
                    return item.colore;
                });
            }
            else serieColors = colorSerie(serieRef.length - 1);
            for (var i = 0; i < _dataJson.categorie.length; i++) {
                labelRef[_dataJson.categorie[i].key] = labelUnescape(_dataJson.categorie[i].label);
            }
            serie = _dataJson.dati.map(function (item) {
                var obj = {};
                var rowdata = [];
                for (var i = 0; i < serieRef.length; i++) {
                    rowdata.push(obj[serieRef[i]] = item[serieRef[i]]);
                }
                return rowdata;
            });

            //Check for no results
            if(noData != 0) {
                tips = _dataJson.dati.map(function (item) {
                    var obj = {};
                    var rowdata = [];
                    for (var i = 0; i < serieRef.length; i++) {
                        rowdata.push(obj[serieRef[i] + '_tooltip'] = labelUnescape(item[serieRef[i] + '_tooltip']));
                    }
                    return rowdata;
                });
                cats = _dataJson.dati.map(function (cat) {
                    return { data: labelUnescape(cat.data), visible: (cat.dataLabel == undefined || cat.dataLabel != '') };
                });
            }
        }
    } else {
        if(_dataJson.hasOwnProperty("dati") && _dataJson.hasOwnProperty("coloriAutomatici")) {
            serieRef = _dataJson.dati.map(function(item){
                return { label: labelUnescape(item.label), value: item.value };
            });
            serie.push(_dataJson.dati.map(function(item) {
                dpChart.pieTotal += item.value;
                return item.value;
            }));
            //Check for no results
            if(noData != 0) {
                tips.push(_dataJson.dati.map(function (item) {
                    return labelUnescape(item.tooltip);
                }));
            }
            if(!_dataJson.coloriAutomatici) {
                serieColors = _dataJson.dati.map(function(item){
                    return item.colore;
                });
            }
            else serieColors = colorSerie(serieRef.length - 1);
            dpChart.legendTooltip = {};
            serieRef = serieRef.map(function(key, index) {
                dpChart.legendTooltip[index] = key.label;
                if(dpChart.limitLegenda !== -1) {
                    if(dpChart.limitLegenda < key.label.length) {
                        var _txt = (index +1) + '. ' + key.label.substr(0, dpChart.limitLegenda) + '...';
                        return (dpChart.valueOnLegend)?_txt+' ('+ key.value + ')':_txt;
                    }
                }
                if(dpChart.valueOnLegend) {
                    return (index +1) + '. ' + key.label + ' (' + key.value + ')';
                }
                return (index +1) + '. ' + key.label;
            });
        }
    }
    if(noData != 0) {
        serie.unshift(serieRef);
    }

    dpChart.type = _type;
    dpChart.size = _size;
    dpChart.rows = (noData != 0)?serie:[];
    dpChart.labelRef = labelRef;
    dpChart.catsTooltip = {};
    dpChart.cats = [];
    if( _type !== 'pie') {
        dpChart.legendTooltip = {};
        Object.keys(dpChart.labelRef).map(function(key) {
            dpChart.legendTooltip[key] = dpChart.labelRef[key];
            if(dpChart.limitLegenda !== -1) {
                if(dpChart.limitLegenda < dpChart.labelRef[key].length) {
                    dpChart.labelRef[key] = labelRef[key].substr(0, dpChart.limitLegenda) + '...';
                }
            }
        });
        dpChart.cats = cats.map(function(key, index) {
            dpChart.catsTooltip[index] = key.data;
            if(dpChart.limit !== -1) {
                if(dpChart.limit < key.data.length) {
                    key.data = key.data.substr(0, dpChart.limit) + '...';
                }
            }
            dpChart.maxCategory = (key.data.length > dpChart.maxCategory.length)?key.data:dpChart.maxCategory;

            return !key.visible?'':key.data;
        });
    }
    dpChart.colors = serieColors;
    dpChart.noData = noData;
    dpChart.noDataLabel = (noData == 0)?noDataLabel:'';
    dpChart.tips = tips;

    return dpChart;
}

/**
 * Elenco colori serie
 * @param ref
 * @returns {string}
 */
function colorSerie(ref) {
    var color = [
        "#2484c1", "#65a620", "#7b6888", "#a05d56", "#961a1a", "#d8d23a", "#e98125", "#d0743c", "#635222", "#6ada6a",
        "#0c6197", "#7d9058", "#207f33", "#44b9b0", "#bca44a", "#e4a14b", "#a3acb2", "#8cc3e9", "#69a6f9", "#5b388f",
        "#546e91", "#8bde95", "#d2ab58", "#273c71", "#98bf6e", "#4daa4b", "#98abc5", "#cc1010", "#31383b", "#006391",
        "#c2643f", "#b0a474", "#a5a39c", "#a9c2bc", "#22af8c", "#7fcecf", "#987ac6", "#3d3b87", "#b77b1c", "#c9c2b6",
        "#807ece", "#8db27c", "#be66a2", "#9ed3c6", "#00644b", "#005064", "#77979f", "#77e079", "#9c73ab", "#1f79a7"
    ];
    if(ref > (color.length - 1)) {
        console.log('Serie grafico superiore a '+color.length);
        return color;
    }
    var range = color.splice(0, (ref + 1));

    return range;
}

/**
 *
 * @param attr
 * @param tx: x translation
 * @param ty: y translation
 * @param replace: replace tx, ty values
 * @returns {*}
 */
function txty(attr, tx, ty, replace) {

    if(attr.indexOf('translate') != -1) {
        var tmp = (attr.indexOf(',') != -1)?attr.split(','):attr.split(' ');
        if(tmp.length == 1) {
            tmp[0] = tmp[0].slice(0, -1);
            tmp.push('0)');
        }
        replace = replace || false;
        if(!replace){
            tmp[0] = parseFloat(tmp[0].split('translate(')[1])+tx;
            tmp[0] = 'translate('+tmp[0];

            tmp[1] = parseFloat(tmp[1].split(')')[0])+ty;
            tmp[1] += ')';
        }
        else {
            tmp[0] = tx;
            tmp[0] = 'translate('+tmp[0];

            tmp[1] = ty;
            tmp[1] += ')';
        }

        return tmp.join(',');
    }
    return attr;
}

function setPolyfill() {
    if (window.NodeList && !NodeList.prototype.forEach) {
        NodeList.prototype.forEach = function (callback, argument) {
            argument = argument || window;
            for (var i = 0; i < this.length; i++) {
                callback.call(argument, this[i], i, this);
            }
        };
    }
}

/***
 *
 * @param _label
 * @returns {string}
 */
function labelUnescape(_label) {
    return _label.replace('&apos;',"'");
}


