function Initialize() {
    container = document.getElementById("Scroller");
    version = detect();

    connectEvents();

    var items = document.querySelectorAll('.items li a');
    var body = document.body;
    document.querySelector('#cta > a').setAttribute('href', document.querySelector('header > p > a').getAttribute('href'));
    var poppy = document.querySelector('#poppy');
    function fun() {
        var me = this === window ?  window.event.srcElement : this;
        document.querySelector('body').setAttribute('class', 'poppy');

        var title = me.querySelector('h2').textContent;
        poppy.querySelector('a[data-id]').textContent = title;
        poppy.querySelector('a[data-id]').setAttribute('href', me.getAttribute('href'));
        var e = arguments[0] || window.event;
        e.preventDefault();
    }
    clicky(document.querySelector("#poppy-hidey"), function() {
        document.querySelector('body').setAttribute('class', '');
    })
    clicky(document.querySelector("#poppy-bg"), function() {
        document.querySelector('body').setAttribute('class', '');
    })
    for ( var i= 0 ; i < items.length; i++ ) {
        clicky(items[i], fun);
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
    if ( version.UA != "MSIE") {
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