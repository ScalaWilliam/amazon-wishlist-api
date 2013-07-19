<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:wl="https://vynar.com/2013/amazon-wishlist" xmlns="http://www.w3.org/1999/xhtml">
    <xsl:template match="wl:wishlist">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <html>
            <head>
                <title>My Wishlist</title>
                <link href="xhtml-wishlist.css" rel="stylesheet" type="text/css"/>
                <script src="xhtml-scroll.js" type="text/javascript"></script>
                <meta name="viewport" content="width=device-width, maximum-scale=1.0" />
            </head>
            <body onload="Initialize();" class="">
                <div id="Container">
                <header>
                    <h1>Wishlist For: <xsl:value-of select="wl:page[1]/wl:name"/></h1>
                    <address><xsl:value-of select="wl:page[1]/wl:address"/></address>
                    <p>See the <a href="{wl:page[1]/wl:url}"><xsl:value-of select="wl:page[1]/wl:name"/> Wishlist on Amazon.co.uk</a></p>
                    <footer>
                        <p>Based on the <a href="https://vynar.com/2013/amazon-wishlist">Amazon Wishlist API.</a> Written in PHP5, DOM, XSLT 1.0, CSS3, JavaScript, SQLite.</p>
                        <p><a href="?id={wl:id}&amp;data=data">View XML</a>, <a href="?id={wl:id}&amp;full=full">View semantic XHTML</a></p>
                    </footer>
                </header>
                <article id="Scroller">
                <ol class="items">
                    <xsl:apply-templates select="wl:page/wl:item">
                        <xsl:sort select="wl:priority/@wl:level" order="descending"/>
                    </xsl:apply-templates>
                </ol>
                </article>
                    <section id="poppy">
                        <div>
                        <h2>On your way to buy a gift for me?</h2>
                        <p>I would be pleased to have:<br/> <a href="" target="_blank" data-id="name">This gift</a><br/> as a gift!</p>
                        <p id="cta"><a href="">Click here to buy it for me!</a></p>
                        <a id="poppy-hidey" target="_blank">&#171;</a>
                        </div>
                    </section>
                    <div id="poppy-bg">
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="wl:item">
        <li>
            <a href="{wl:link}">
            <h2><xsl:value-of select="wl:title"/></h2>
            <figure>
                <img src="{wl:image/@wl:src}"/>
                <span class="background" style="background-image:url('{wl:image/@wl:src}')"></span>
            </figure>
            <xsl:if test="wl:price">
                <h3><xsl:value-of select="wl:price"/></h3>
            </xsl:if>
                <span class="priority"><span class="heart heart-{wl:priority/@wl:level}"></span></span>
            </a>
        </li>
    </xsl:template>
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>