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

/**
 * Polyfill per Internet Explorer 11
 *
 * Questo file fornisce implementazioni delle funzioni ES6+ non supportate
 * nativamente da IE11, permettendo al codice moderno di funzionare anche
 * su browser legacy.
 *
 * IMPORTANTE: Questo file deve essere caricato PRIMA di qualsiasi altro
 * script JavaScript nella pagina.
 */

(function() {
    'use strict';

    // =========================================================================
    // Number polyfills
    // =========================================================================

    /**
     * Number.parseInt()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/parseInt
     */
    if (!Number.parseInt) {
        Number.parseInt = parseInt;
    }

    /**
     * Number.parseFloat()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/parseFloat
     */
    if (!Number.parseFloat) {
        Number.parseFloat = parseFloat;
    }

    /**
     * Number.isNaN()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/isNaN
     *
     * Nota: Number.isNaN() e' piu' rigoroso di isNaN() globale.
     * isNaN("hello") => true
     * Number.isNaN("hello") => false
     */
    if (!Number.isNaN) {
        Number.isNaN = function(value) {
            return typeof value === 'number' && isNaN(value);
        };
    }

    /**
     * Number.isFinite()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/isFinite
     */
    if (!Number.isFinite) {
        Number.isFinite = function(value) {
            return typeof value === 'number' && isFinite(value);
        };
    }

    /**
     * Number.isInteger()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/isInteger
     */
    if (!Number.isInteger) {
        Number.isInteger = function(value) {
            return typeof value === 'number' &&
                   isFinite(value) &&
                   Math.floor(value) === value;
        };
    }

    /**
     * Number.isSafeInteger()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/isSafeInteger
     */
    if (!Number.isSafeInteger) {
        Number.isSafeInteger = function(value) {
            return Number.isInteger(value) &&
                   Math.abs(value) <= Number.MAX_SAFE_INTEGER;
        };
    }

    /**
     * Number.MAX_SAFE_INTEGER
     */
    if (!Number.MAX_SAFE_INTEGER) {
        Number.MAX_SAFE_INTEGER = 9007199254740991; // Math.pow(2, 53) - 1
    }

    /**
     * Number.MIN_SAFE_INTEGER
     */
    if (!Number.MIN_SAFE_INTEGER) {
        Number.MIN_SAFE_INTEGER = -9007199254740991;
    }

    /**
     * Number.EPSILON
     */
    if (!Number.EPSILON) {
        Number.EPSILON = 2.220446049250313e-16; // Math.pow(2, -52)
    }

    // =========================================================================
    // String polyfills
    // =========================================================================

    /**
     * String.prototype.startsWith()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/startsWith
     */
    if (!String.prototype.startsWith) {
        String.prototype.startsWith = function(searchString, position) {
            position = position || 0;
            return this.indexOf(searchString, position) === position;
        };
    }

    /**
     * String.prototype.endsWith()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/endsWith
     */
    if (!String.prototype.endsWith) {
        String.prototype.endsWith = function(searchString, length) {
            if (length === undefined || length > this.length) {
                length = this.length;
            }
            return this.substring(length - searchString.length, length) === searchString;
        };
    }

    /**
     * String.prototype.includes()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/includes
     */
    if (!String.prototype.includes) {
        String.prototype.includes = function(searchString, position) {
            if (searchString instanceof RegExp) {
                throw new TypeError('String.prototype.includes does not accept a RegExp');
            }
            if (position === undefined) {
                position = 0;
            }
            return this.indexOf(searchString, position) !== -1;
        };
    }

    /**
     * String.prototype.repeat()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/repeat
     */
    if (!String.prototype.repeat) {
        String.prototype.repeat = function(count) {
            if (this === null || this === undefined) {
                throw new TypeError('String.prototype.repeat called on null or undefined');
            }
            var str = '' + this;
            count = +count;
            if (count < 0 || count === Infinity) {
                throw new RangeError('Invalid count value');
            }
            count = Math.floor(count);
            if (str.length === 0 || count === 0) {
                return '';
            }
            var result = '';
            while (count > 0) {
                if (count & 1) {
                    result += str;
                }
                count >>>= 1;
                if (count > 0) {
                    str += str;
                }
            }
            return result;
        };
    }

    /**
     * String.prototype.padStart()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/padStart
     */
    if (!String.prototype.padStart) {
        String.prototype.padStart = function(targetLength, padString) {
            targetLength = targetLength >> 0;
            padString = String(padString !== undefined ? padString : ' ');
            if (this.length >= targetLength || padString.length === 0) {
                return String(this);
            }
            targetLength = targetLength - this.length;
            if (targetLength > padString.length) {
                padString += padString.repeat(Math.ceil(targetLength / padString.length));
            }
            return padString.slice(0, targetLength) + String(this);
        };
    }

    /**
     * String.prototype.padEnd()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/padEnd
     */
    if (!String.prototype.padEnd) {
        String.prototype.padEnd = function(targetLength, padString) {
            targetLength = targetLength >> 0;
            padString = String(padString !== undefined ? padString : ' ');
            if (this.length >= targetLength || padString.length === 0) {
                return String(this);
            }
            targetLength = targetLength - this.length;
            if (targetLength > padString.length) {
                padString += padString.repeat(Math.ceil(targetLength / padString.length));
            }
            return String(this) + padString.slice(0, targetLength);
        };
    }

    /**
     * String.prototype.replaceAll()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/replaceAll
     */
    if (!String.prototype.replaceAll) {
        String.prototype.replaceAll = function(search, replacement) {
            var target = this;
            if (search instanceof RegExp) {
                if (!search.global) {
                    throw new TypeError('replaceAll must be called with a global RegExp');
                }
                return target.replace(search, replacement);
            }
            return target.split(search).join(replacement);
        };
    }

    /**
     * String.prototype.trim() - gia' supportato in IE9+, ma aggiungiamo per sicurezza
     */
    if (!String.prototype.trim) {
        String.prototype.trim = function() {
            return this.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
        };
    }

    /**
     * String.prototype.trimStart() / trimLeft()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/trimStart
     */
    if (!String.prototype.trimStart) {
        String.prototype.trimStart = function() {
            return this.replace(/^[\s\uFEFF\xA0]+/, '');
        };
    }
    if (!String.prototype.trimLeft) {
        String.prototype.trimLeft = String.prototype.trimStart;
    }

    /**
     * String.prototype.trimEnd() / trimRight()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/trimEnd
     */
    if (!String.prototype.trimEnd) {
        String.prototype.trimEnd = function() {
            return this.replace(/[\s\uFEFF\xA0]+$/, '');
        };
    }
    if (!String.prototype.trimRight) {
        String.prototype.trimRight = String.prototype.trimEnd;
    }

    // =========================================================================
    // Array polyfills
    // =========================================================================

    /**
     * Array.prototype.includes()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/includes
     */
    if (!Array.prototype.includes) {
        Array.prototype.includes = function(searchElement, fromIndex) {
            if (this === null || this === undefined) {
                throw new TypeError('Array.prototype.includes called on null or undefined');
            }
            var o = Object(this);
            var len = o.length >>> 0;
            if (len === 0) {
                return false;
            }
            var n = fromIndex | 0;
            var k = Math.max(n >= 0 ? n : len - Math.abs(n), 0);
            while (k < len) {
                if (o[k] === searchElement ||
                    (searchElement !== searchElement && o[k] !== o[k])) { // NaN check
                    return true;
                }
                k++;
            }
            return false;
        };
    }

    /**
     * Array.prototype.find()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/find
     */
    if (!Array.prototype.find) {
        Array.prototype.find = function(predicate, thisArg) {
            if (this === null || this === undefined) {
                throw new TypeError('Array.prototype.find called on null or undefined');
            }
            if (typeof predicate !== 'function') {
                throw new TypeError('predicate must be a function');
            }
            var list = Object(this);
            var length = list.length >>> 0;
            for (var i = 0; i < length; i++) {
                var value = list[i];
                if (predicate.call(thisArg, value, i, list)) {
                    return value;
                }
            }
            return undefined;
        };
    }

    /**
     * Array.prototype.findIndex()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/findIndex
     */
    if (!Array.prototype.findIndex) {
        Array.prototype.findIndex = function(predicate, thisArg) {
            if (this === null || this === undefined) {
                throw new TypeError('Array.prototype.findIndex called on null or undefined');
            }
            if (typeof predicate !== 'function') {
                throw new TypeError('predicate must be a function');
            }
            var list = Object(this);
            var length = list.length >>> 0;
            for (var i = 0; i < length; i++) {
                if (predicate.call(thisArg, list[i], i, list)) {
                    return i;
                }
            }
            return -1;
        };
    }

    /**
     * Array.prototype.fill()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/fill
     */
    if (!Array.prototype.fill) {
        Array.prototype.fill = function(value, start, end) {
            if (this === null || this === undefined) {
                throw new TypeError('Array.prototype.fill called on null or undefined');
            }
            var o = Object(this);
            var len = o.length >>> 0;
            var relativeStart = start >> 0;
            var k = relativeStart < 0 ?
                    Math.max(len + relativeStart, 0) :
                    Math.min(relativeStart, len);
            var relativeEnd = end === undefined ? len : end >> 0;
            var final = relativeEnd < 0 ?
                        Math.max(len + relativeEnd, 0) :
                        Math.min(relativeEnd, len);
            while (k < final) {
                o[k] = value;
                k++;
            }
            return o;
        };
    }

    /**
     * Array.from()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/from
     */
    if (!Array.from) {
        Array.from = function(arrayLike, mapFn, thisArg) {
            if (arrayLike === null || arrayLike === undefined) {
                throw new TypeError('Array.from requires an array-like object');
            }
            var items = Object(arrayLike);
            var len = items.length >>> 0;
            var result = new Array(len);
            var hasMapFn = typeof mapFn === 'function';
            for (var i = 0; i < len; i++) {
                var value = items[i];
                result[i] = hasMapFn ? mapFn.call(thisArg, value, i) : value;
            }
            return result;
        };
    }

    /**
     * Array.of()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/of
     */
    if (!Array.of) {
        Array.of = function() {
            return Array.prototype.slice.call(arguments);
        };
    }

    /**
     * Array.prototype.copyWithin()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/copyWithin
     */
    if (!Array.prototype.copyWithin) {
        Array.prototype.copyWithin = function(target, start, end) {
            if (this === null || this === undefined) {
                throw new TypeError('Array.prototype.copyWithin called on null or undefined');
            }
            var o = Object(this);
            var len = o.length >>> 0;
            var relativeTarget = target >> 0;
            var to = relativeTarget < 0 ?
                     Math.max(len + relativeTarget, 0) :
                     Math.min(relativeTarget, len);
            var relativeStart = start >> 0;
            var from = relativeStart < 0 ?
                       Math.max(len + relativeStart, 0) :
                       Math.min(relativeStart, len);
            var relativeEnd = end === undefined ? len : end >> 0;
            var final = relativeEnd < 0 ?
                        Math.max(len + relativeEnd, 0) :
                        Math.min(relativeEnd, len);
            var count = Math.min(final - from, len - to);
            var direction = 1;
            if (from < to && to < (from + count)) {
                direction = -1;
                from += count - 1;
                to += count - 1;
            }
            while (count > 0) {
                if (from in o) {
                    o[to] = o[from];
                } else {
                    delete o[to];
                }
                from += direction;
                to += direction;
                count--;
            }
            return o;
        };
    }

    // =========================================================================
    // Object polyfills
    // =========================================================================

    /**
     * Object.assign()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/assign
     */
    if (!Object.assign) {
        Object.assign = function(target) {
            if (target === null || target === undefined) {
                throw new TypeError('Cannot convert undefined or null to object');
            }
            var to = Object(target);
            for (var index = 1; index < arguments.length; index++) {
                var nextSource = arguments[index];
                if (nextSource !== null && nextSource !== undefined) {
                    for (var nextKey in nextSource) {
                        if (Object.prototype.hasOwnProperty.call(nextSource, nextKey)) {
                            to[nextKey] = nextSource[nextKey];
                        }
                    }
                }
            }
            return to;
        };
    }

    /**
     * Object.entries()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/entries
     */
    if (!Object.entries) {
        Object.entries = function(obj) {
            if (obj === null || obj === undefined) {
                throw new TypeError('Cannot convert undefined or null to object');
            }
            var ownProps = Object.keys(obj);
            var i = ownProps.length;
            var resArray = new Array(i);
            while (i--) {
                resArray[i] = [ownProps[i], obj[ownProps[i]]];
            }
            return resArray;
        };
    }

    /**
     * Object.values()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/values
     */
    if (!Object.values) {
        Object.values = function(obj) {
            if (obj === null || obj === undefined) {
                throw new TypeError('Cannot convert undefined or null to object');
            }
            var vals = [];
            for (var key in obj) {
                if (Object.prototype.hasOwnProperty.call(obj, key)) {
                    vals.push(obj[key]);
                }
            }
            return vals;
        };
    }

    /**
     * Object.keys() - supportato in IE9+, ma aggiungiamo per sicurezza
     */
    if (!Object.keys) {
        Object.keys = function(obj) {
            if (obj !== Object(obj)) {
                throw new TypeError('Object.keys called on a non-object');
            }
            var keys = [];
            for (var key in obj) {
                if (Object.prototype.hasOwnProperty.call(obj, key)) {
                    keys.push(key);
                }
            }
            return keys;
        };
    }

    // =========================================================================
    // Math polyfills
    // =========================================================================

    /**
     * Math.sign()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/sign
     */
    if (!Math.sign) {
        Math.sign = function(x) {
            x = +x;
            if (x === 0 || isNaN(x)) {
                return x;
            }
            return x > 0 ? 1 : -1;
        };
    }

    /**
     * Math.trunc()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/trunc
     */
    if (!Math.trunc) {
        Math.trunc = function(x) {
            return x < 0 ? Math.ceil(x) : Math.floor(x);
        };
    }

    /**
     * Math.log2()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/log2
     */
    if (!Math.log2) {
        Math.log2 = function(x) {
            return Math.log(x) * Math.LOG2E;
        };
    }

    /**
     * Math.log10()
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/log10
     */
    if (!Math.log10) {
        Math.log10 = function(x) {
            return Math.log(x) * Math.LOG10E;
        };
    }

    // =========================================================================
    // Console polyfills (per IE senza console aperta)
    // =========================================================================

    if (typeof console === 'undefined') {
        window.console = {
            log: function() {},
            warn: function() {},
            error: function() {},
            info: function() {},
            debug: function() {}
        };
    }

    // =========================================================================
    // CustomEvent polyfill
    // https://developer.mozilla.org/en-US/docs/Web/API/CustomEvent/CustomEvent
    // =========================================================================

    if (typeof window.CustomEvent !== 'function') {
        function CustomEvent(event, params) {
            params = params || { bubbles: false, cancelable: false, detail: null };
            var evt = document.createEvent('CustomEvent');
            evt.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
            return evt;
        }
        CustomEvent.prototype = window.Event.prototype;
        window.CustomEvent = CustomEvent;
    }

    // =========================================================================
    // Element.prototype.closest() polyfill
    // https://developer.mozilla.org/en-US/docs/Web/API/Element/closest
    // =========================================================================

    if (!Element.prototype.closest) {
        Element.prototype.closest = function(s) {
            var el = this;
            do {
                if (Element.prototype.matches.call(el, s)) {
                    return el;
                }
                el = el.parentElement || el.parentNode;
            } while (el !== null && el.nodeType === 1);
            return null;
        };
    }

    // =========================================================================
    // Element.prototype.matches() polyfill
    // https://developer.mozilla.org/en-US/docs/Web/API/Element/matches
    // =========================================================================

    if (!Element.prototype.matches) {
        Element.prototype.matches =
            Element.prototype.msMatchesSelector ||
            Element.prototype.webkitMatchesSelector;
    }

})();
