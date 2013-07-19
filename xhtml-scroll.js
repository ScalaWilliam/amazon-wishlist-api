
function Initialize() {
    container = document.getElementById("Scroller");
    version = detect();
    connectEvents();
    var body = document.body;
    document.querySelector('#cta > a').setAttribute('href', document.querySelector('header > p > a').getAttribute('href'));
    var poppy = document.querySelector('#poppy');
    var body = document.querySelector('body');
    var aname = poppy.querySelector('a.name');
    clicky(document.querySelector("#poppy-hidey"),noPoppy);
    clicky(document.querySelector("#poppy-bg"), noPoppy);

    var items = document.querySelectorAll('.items li a');
    for ( var i= 0 ; i < items.length; i++ ) {
        clicky(items[i], fun);
    }
    function fun() {
        var e = arguments.length ? arguments[0] : window.event;
        var me = this === window ?  window.event.srcElement : this;

        var title = me.querySelector('h2').textContent;
        aname.textContent = title;
        aname.setAttribute('href', me.getAttribute('href'));
        e.preventDefault();
        herePoppy();
        return false;
    }
    function noPoppy() {
        body.setAttribute('class', '');
    }
    function herePoppy() {
        body.setAttribute('class', 'poppy');
    }
}

function clicky(node, fun) {
    if ( !node ) return;
    if ( "addEventListener" in node ) {
        node.addEventListener("click", fun, false);
    }
    else if ( "attachEvent" in node ) {
        node.attachEvent("onclick", fun);
    }
}




var container;
var version;

function checkForTouch() {
    if (!window.clientInformation.msPointerEnabled ||
        window.clientInformation.msMaxTouchPoints == 0) {
        var touchGroup = document.getElementById("TouchGroup");
        touchGroup.style.display = "none";
    }
}

function detect() {
    var browsers = {
        'Chrome': [/Chrome\/(\S+)/], 'Firefox': [/Firefox\/(\S+)/], 'MSIE': [/MSIE (\S+);/], 'Opera':
            [   /Opera\/.*?Version\/(\S+)/,     /* Opera 10 */
                /Opera\/(\S+)/                  /* Opera 9 and older */
            ], 'Safari': [/Version\/(\S+).*?Safari\//]
    };

    var b, m, userAgent, vendor, version, platform;
    var elements = 1;
    userAgent = navigator.userAgent;

    for (vendor in browsers)
    {
        while (b = browsers[vendor].shift()) {
            if (m = userAgent.match(b)) {
                if (!(vendor === 'Firefox' && userAgent.indexOf("Trident") !== -1))
                {
                    version = (m[1].match(new RegExp('[^.]+(?:\.[^.]+){0,' + --elements + '}')))[0];
                    return {
                        UA: vendor,
                        version: version,
                        platform: platform
                    };
                }
            }
        }
    }

    if (userAgent.indexOf("Trident") !== -1)
    {
        return {
            UA: 'MSIE',
            version: 11,
            platform: undefined
        };
    }
}


function mouseWheel(e) {
    var event = e || window.event;
    var data;

    if (container.doScroll) {
        container.doScroll(event.wheelDelta > 0 ? "left" : "right");
    }
    else {
        data = event.wheelDelta || (event.detail * -20);
        container.scrollLeft -= data;
    }
    return false;

}


function connectMouseWheel() {
    var body = document.querySelector("body");
    if ( !(version && "UA" in version && version.UA == "MSIE") ) {
        if ("onmousewheel" in body) {
            body.addEventListener("mousewheel", mouseWheel, false);
        }
        else {
            body.addEventListener("DOMMouseScroll", mouseWheel);
        }
    }
}

function connectEvents() {
    connectMouseWheel();
}