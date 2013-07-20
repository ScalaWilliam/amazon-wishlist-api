Element.prototype.$ = function(q) {
    return this.querySelector(q)
}
Element.prototype.$$ = function(q) {
    return this.querySelectorAll(q)
}
function Initialize() {


    var link = $('#wl-link').getAttribute('href')
    var aname = $('#poppy a.name')
    $('#cta > a').setAttribute('href', link)

    clicky($("#poppy-hidey"),noPoppy)
    clicky($("#poppy-bg"), noPoppy)

    var items = $$('.items li a');
    for ( var i= 0 ; i < items.length; i++ ) {
        clicky(items[i], clickItem);
    }


    var container = document.getElementById("Scroller");
    setupMouse(container);

    function clickItem() {
        var e = arguments.length ? arguments[0] : window.event;
        var me = this === window ?  window.event.srcElement : this;
        var title = me.$('h2').textContent;
        var link = me.getAttribute('href');

        aname.textContent = title;
        aname.setAttribute('href', link);
        herePoppy();

        e.preventDefault();
        return false;
    }

    function noPoppy() {
        document.body.setAttribute('class', '');
    }
    function herePoppy() {
        document.body.setAttribute('class', 'poppy');
    }
    function $(q){
        return document.body.$(q)
    }
    function $$(q){
        return document.body.$$(q)
    }
}

function clicky(node, fun) {
    if ( !node ) return;
    if ( "addEventListener" in node )
        return node.addEventListener("click", fun, false);
    else if ( "attachEvent" in node )
        return node.attachEvent("onclick", fun);
}



function setupMouse(container) {
    var body = document.querySelector("body");
    if ( "attachEvent" in body ) return;
    if ( "onmousewheel" in body )
        return body.addEventListener("mousewheel", onWheel, false);
    return body.addEventListener("DOMMouseScroll", onWheel);

    function onWheel(e) {
        var event = e ? e : window.event;
        var dx;

        if ("doScroll" in container && container.doScroll) {
            container.doScroll(event.wheelDelta > 0 ? "left" : "right");
        }
        else {
            dx = event.wheelDelta || (event.detail * -20);
            container.scrollLeft -= dx;
        }
        return false;
    }
}
