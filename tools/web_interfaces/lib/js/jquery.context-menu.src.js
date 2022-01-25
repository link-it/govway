/**
 *      @author İsa Eken <hello@isaeken.com.tr>
 *      @website https://www.isaeken.com.tr
 *      @github https://www.github.com/isaeken
 *      @name jquery.context-menu
 * 
 * Copyright (c) 2020 İsa Eken
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 
 */

/**
 * Welcome, we start!
 */
//console.log(
//    '%cjquery.context-menu developed by %cİsa Eken <hello@isaeken.com.tr>',
//    'font-size: larger; background-color: black; color: white;',
//    'font-size: larger; background-color: black; color: white; font-weight: bold;'
//);

/**
 * Check jQuery plugin is installed.
 */
if (!window.jQuery) throw new Error('jQuery plugin is required!');

/**
 * Mouse buttons
 * @type {{LEFT: string, RIGHT: string, BOTH: string}}
 */
const mouseButton = {
    LEFT    : 1,
    MIDDLE  : 2,
    RIGHT   : 3,
};

/**
 * jQuery Context Menu
 */
(function ($) {

    /**
     * Set 'true' to turn on debug mode.
     * @type {boolean}
     */
    var debug = false;

    /**
     * Cursor positions.
     * @type {{x: number, y: number}}
     */
    var cursorPosition = { x: 0, y: 0 };

    /**
     * Context menu sizes.
     * @type {{paddingBottom: number, paddingRight: number, width: number, paddingTop: number, paddingLeft: number, height: number}}
     */
    var contextMenuSize = { width: 0, height: 0, paddingLeft: 0, paddingRight: 0, paddingTop: 0, paddingBottom: 0 };

    /**
     * Animation time.
     * @type {number}
     */
    var animationTime = 150;

    /**
     * LOG function.
     * @param data
     * @param isError
     * @returns {*}
     * @constructor
     */
    function LOG(data, isError = false) {
        /**
         * Check if data parameter exists.
         */
        if (!data) throw new Error('data parameter is required!');

        /**
         * Prefix message.
         * @type {string}
         */
        var prefix = 'jquery.context-menu: ';

        /**
         * Suffix message.
         * @type {string}
         */
        var suffix = '';

        /**
         * Check this log is error.
         */
        if (isError) throw new Error(prefix + data + suffix);

        /**
         * Log if debug mode on.
         */
        if (debug) console.log(
            '%c' + prefix + '%c' + data + '%c' + suffix,
            'background-color: aqua; color: black; font-weight: bold; margin-right: 8px;',
            '',
            ''
        );

        /**
         * Return message.
         */
        return data;
    }

    /**
     * Get max z-index in document.
     * @returns {number}
     */
    function maxZIndex() {
        /**
         * Set default z-index 0
         * @type {number}
         */
        var zindex = 0;

        /**
         * Each all elements.
         */
        $('*').each(function () {
            /**
             * Get element z-index.
             * @type {number}
             * @private
             */
            var _zindex = parseInt($(this).css('z-index'));

            /**
             * Check z-index update if high.
             */
            if (_zindex > zindex) zindex = _zindex;
        });

        return zindex + 1;
    }

    /**
     * Open context menu with animation.
     * @param event
     */
    $.fn.contextMenuOpenAnimation = function (event = function () { }) {
        /**
         * Context menu positions.
         * @type {{top: number, left: number}}
         */
        var position = { left: cursorPosition.x, top: cursorPosition.y };

        /**
         * Check context menu location horizontally.
         */
        if (position.left + $(this).outerWidth(true) > $(window).width())
            position.left = $(window).width() - $(this).outerWidth(true);

        /**
         * Check context menu location vertically.
         */
        if (position.top + $(this).outerHeight(true) > $(window).height())
            position.top = $(window).height() - $(this).outerHeight(true) - 40;

        /**
         * Check context menu height
         */
        if (contextMenuSize.height > $(window).height()) {
            position.top = 2;
            $(this).css('height', $(window).height() - 40);
        }
        else $(this).css('height', 'unset');

        /**
         * Set context menu styles.
         */
        $(this).css('display', 'block');
        $(this).css('padding', '0');
        $(this).css('opacity', '0');
        $(this).css('z-index', maxZIndex());
        $(this).css('top', position.top);
        $(this).css('left', position.left);

        /**
         * Animate context menu.
         */
        $(this).animate({
            'padding-top': contextMenuSize.paddingTop,
            'padding-bottom': contextMenuSize.paddingBottom,
            'padding-left': contextMenuSize.paddingLeft,
            'padding-right': contextMenuSize.paddingRight,
            'opacity': 1
        }, animationTime, function () {
            // Execute event after context menu openned.
            event();
        });
    }

    /**
     * Close context menu with animation.
     * @param event
     */
    $.fn.contextMenuCloseAnimation = function (event = function () { }) {
        // Context menu closing animation.
        $(this).animate({
            'padding-top': 0,
            'padding-bottom': 0,
            'padding-left': 0,
            'padding-right': 0,
            'opacity': 0
        }, animationTime, function () {
            // Set display none after animation.
            $(this).css('display', 'none');

            // Execute event after closing completed.
            event();
        });
    }

    /**
     * jQuery Context Menu
     * @author İsa Eken <hello@isaeken.com.tr>
     * @returns {{button: number, init: init, onopen: onopen, onclose: onclose, onclick: onclick, update: update, menu: (function(): {addItem: (function(*=, *=): []), removeItem: (function(*): []), items: [], element: (function(): *|jQuery|HTMLElement)}), close: close, open: open}}
     */
    $.fn.contextMenu = function () {
        LOG('Context menu created.');

        // Variables
        var element = this;
        var menuItems = [];
        var menuElement = $('<div class="context-menu"></div>');

        /**
         * Update mouse positions.
         */
        $(window).mousemove(function (event) {
            cursorPosition = {
                x: event.pageX - $(window).scrollLeft(),
                y: event.pageY - $(window).scrollTop()
            };
        });

        return {
            /**
             * Context menu trigger button.
             */
            'button'    : mouseButton.RIGHT,

            /**
             * Context menu base.
             * @returns {{addItem: (function(*=, *=): []), removeItem: (function(*): []), items: [], element: (function(): *|jQuery|HTMLElement)}}
             */
            'menu'      : function () {
                var _this = this;
                return {
                    /**
                     * Context menu items.
                     */
                    'items': menuItems,

                    /**
                     * Add item to context menu items.
                     * @param text
                     * @param click
                     * @returns {[]}
                     */
                    'addItem' : function (text, click = function () {  }) {

                        LOG('Adding new item to context menu.');

                        /**
                         * Check trigger is function or string
                         */
                        if (typeof click !== 'function' && typeof click === 'string') {
                            var link = click;
                            click = function () { window.location.href = link };
                            LOG('Item trigger is a link.');
                        }
                        else if (typeof click !== 'function')
                            LOG('The "click" variable can only be a function or string!', true);

                        /**
                         * Add item to context menu items.
                         */
                        menuItems.push({
                            'text'  : text,
                            'click' : click,
                        });

                        LOG('Added a new item in to context menu.');
                        return _this.items = menuItems;
                    },

                    /**
                     * Remove item in context menu items.
                     * @param id
                     * @returns {[]}
                     */
                    'removeItem': function (id) {
                        LOG('Removing a item (' + id + ') in to context menu items.');

                        var newMenuItems = [];
                        _this.items.forEach(function (item, index) {
                            if (id !== index)
                                newMenuItems.push(item);
                        });

                        LOG('Removed a item (' + id + ') in to context menu items.');
                        menuItems = newMenuItems;
                        return _this.items = menuItems;
                    },

                    /**
                     * Generate context menu HTML.
                     * @returns {*|jQuery|HTMLElement}
                     */
                    'element' : function () {
                        LOG('Generating context menu HTML.');

                        var _menu_content = $('<ul></ul>');
                        menuElement.html('');
                        menuElement.append(_menu_content);

                        /**
                         * Adding context menu items to HTML.
                         */
                        _this.items.forEach(function (item, index) {
                            var _menu_content_item = $('<a></a>');
                            _menu_content_item.html(item['text']);
                            _menu_content_item.on('click', function (event) {
                                item['click']();
                                _this.onclick();
                            });
                            _menu_content.append($('<li></li>').append(_menu_content_item));
                        });

                        LOG('Generated context menu HTML.');
                        return menuElement;
                    },
                }
            },

            /**
             * Context menu open function.
             */
            'open'      :  function (event = function () { }) {
                LOG('Opening context menu.');

                /**
                 * Get all printed context menus
                 */
                $('.context-menu').each(function () {
                    /**
                     * Check context menu is openned
                     */
                    if ($(this).css('display') !== 'none')
                        /**
                         * Close context menu
                         */
                        $(this).contextMenuCloseAnimation();
                })

                menuElement.contextMenuOpenAnimation(event());
                this.onopen();
            },

            /**
             * Context menu close function.
             */
            'close'     : function (event = function () { }) {
                var _this = this;
                LOG('Closing context menu.');
                menuElement.contextMenuCloseAnimation(function () {
                    // Close context menu after update this.
                    _this.update();
                    event();
                });
                this.onclose();
            },

            /**
             * Context menu open event.
             */
            'onopen'    : function () {
                LOG('Context menu openned.');
            },

            /**
             * Context menu close event.
             */
            'onclose'   : function () {
                LOG('Context menu closed.');
            },

            /**
             * Context menu clicked event.
             */
            'onclick'   : function () {
                this.close();
            },

            /**
             * Context menu initialize function.
             */
            'init'      : function () {
                var _this = this;
                LOG('Initializing context menu.');

                /**
                 * Check button is defined.
                 */
                if (!this.button) LOG('Button parameter is required!', true);

                /**
                 * Check is any item added in the context menu.
                 */
                if (this.menu().items.length < 1) LOG('No any items added!', true);

                /**
                 * Add context menu to body and hide.
                 */
                $('body').append(this.menu().element);
                this.menu().element().hide();

                /**
                 * Get context menu sizes.
                 * @type {{paddingBottom: jQuery, paddingRight: jQuery, width: *, paddingTop: jQuery, paddingLeft: jQuery, height: *}}
                 */
                contextMenuSize = {
                    width:          this.menu().element().outerWidth(true),
                    height:         this.menu().element().outerHeight(true),
                    paddingTop:     this.menu().element().css('padding-top'),
                    paddingBottom:  this.menu().element().css('padding-bottom'),
                    paddingLeft:    this.menu().element().css('padding-left'),
                    paddingRight:   this.menu().element().css('padding-right'),
                };

                /**
                 * Trigger element mouse down event.
                 */
                element.mousedown(function (event) {
                    LOG('Trigger element clicked.');
                    LOG('Triggered mouse button: ' + event.which + ', Required mouse button: ' + _this.button);

                    /**
                     * Break all events
                     */
                    event.stopPropagation();

                    /**
                     * Get all printed context menus
                     */
                    $('.context-menu').each(function () {
                        /**
                         * Check context menu is not this
                         */
                        if ($(this) !== menuElement)
                            /**
                             * Check context menu is openned
                             */
                            if ($(this).css('display') !== 'none')
                                /**
                                 * Close context menu
                                 */
                                $(this).contextMenuCloseAnimation();
                    });

                    /**
                     * Check clicked mouse button is required button
                     */
                    if (event.which !== _this.button) _this.close();
                    else {
                        /**
                         * Check is context menu openned
                         */
                        if (menuElement.css('display') !== 'none') {
                            /**
                             * Close context menu
                             */
                            _this.close(function () {
                                /**
                                 * Open context menu
                                 */
                                _this.open();
                            });
                        }
                        /**
                         * Just open
                         */
                        else _this.open();
                    }
                });

                /**
                 * Disable built-in context menu in trigger element.
                 */
                element.contextMenu(function () {
                    return false;
                });
                
                /**
                 * Chiusura automatica del menu' se si sposta il mouse fuori dall'area del menu' e non si clicca per piu' di 1.5 secondi. 
                 */
                this.menu().element().mouseleave(function () {
                	LOG('Mouse fuori dal menu');
                	$(this).delay( 1000 ).contextMenuCloseAnimation();
                });

                /**
                 * Document mouse down event.
                 */
                $(document).mousedown(function (event) {
                    var target = $(event.target);
                    var target_tag = target.attr('tagName');

                    var close = true;

                    if (target_tag === 'A')
                        if (target.parent().attr('tagName') === 'LI')
                            if (target.parent().parent().attr('tagName') === 'UL')
                                if (target.parent().parent().parent().hasClass('context-menu'))
                                    close = false;

                    if (target_tag === 'LI')
                        if (target.parent().attr('tagName') === 'UL')
                            if (target.parent().parent().hasClass('context-menu'))
                                close = false;

                    if (target_tag === 'UL')
                        if (target.parent().hasClass('context-menu'))
                            close = false;

                    if (target.hasClass('context-menu'))
                        close = false;

                    //
                    if (close) _this.close();

                    // if (!target.parents('.context-menu')) _this.close()
                });

                /**
                 * ESC press event
                 */
                $(document).keyup(function (event) {
                    /**
                     * Check pressed key is ESC
                     */
                    if (event.keyCode === 27)
                        /**
                         * Get all printed context menus
                         */
                        $('.context-menu').each(function () {
                            /**
                             * Check context menu is openned
                             */
                            if ($(this).css('display') !== 'none')
                                /**
                                 * Close context menu
                                 */
                                $(this).contextMenuCloseAnimation();
                        });
                });
            },

            /**
             * Update printed context menu HTML.
             */
            'update'    : function () {
                LOG('Updating context menu.');
                $('body').append(this.menu().element);
                this.menu().element().hide();
            }
        }
    }

    /**
     * Auto generate context menus.
     */
    $.autoInitializeContextMenus = function () {
        $('.context-menu-trigger').each(function () {
            LOG('Founded a HTML trigger.');

            /**
             * Check trigger have a content.
             */
            if (!$(this).has('.context-menu-content')) LOG('This trigger not have a content.');

            /**
             * Check context menu content have a any item.
             */
            else if (!$(this).children('.context-menu-content').has('li a')) LOG('This trigger content not have a any items.');

            /**
             * OK
             */
            else {

                /**
                 * Create context menu.
                 * @type {jQuery|{button: number, init: init, onopen: onopen, onclose: onclose, onclick: onclick, update: update, menu: (function(): {addItem: (function(*=, *=): *[]), removeItem: (function(*): *[]), items: *[], element: ((function(): *)|jQuery|HTMLElement)}), close: close, open: open}}
                 */
                var contextMenu = $(this).contextMenu();

                /**
                 * Set context menu button.
                 */
                if ($(this).hasClass('context-menu-button-right')) contextMenu.button = mouseButton.RIGHT;
                else if ($(this).hasClass('context-menu-button-middle')) contextMenu.button = mouseButton.MIDDLE;
                else contextMenu.button = mouseButton.LEFT;

                /**
                 * Get context menu items.
                 */
                $(this).children('.context-menu-content').children('li').each(function () {
                    /**
                     * Check item has a element.
                     */
                    if ($(this).has('a')) {

                        var link = $(this).children('a').first();

                        /**
                         * Context menu item text.
                         * @type {null}
                         */
                        var text = null;

                        /**
                         * Context menu item click event.
                         * @type {null}
                         */
                        var click = null;

                        /**
                         * Set Context menu item text.
                         * @type {jQuery|string}
                         */
                        text = link.html();

                        /**
                         *
                         * @type {jQuery|undefined}
                         */
                        click = link.attr('href');
                        if (!(typeof click !== typeof undefined && click !== false))
                            click = new Function(link.attr('onclick'));

                        /**
                         * Add item to context menu.
                         */
                        contextMenu.menu().addItem(text, click);
                    }
                });

                /**
                 * Initialize context menu.
                 */
                contextMenu.init();
            }
        });
    }

}) (jQuery);

/**
 * Auto generate context menus.
 */
$(document).ready(function () {
    $.autoInitializeContextMenus();
});
