<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:wl="https://vynar.com/2013/amazon-wishlist" xmlns="http://www.w3.org/1999/xhtml">
    <xsl:template match="wl:wishlist">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <html>
            <head>
                <title>My Wishlist</title>
                <link href="xhtml-wishlist.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
                <header>
                    <h1>Wishlist For: <xsl:value-of select="wl:page[1]/wl:name"/></h1>
                    <address><xsl:value-of select="wl:page[1]/wl:address"/></address>
                    <p>See the <a href="{wl:page[1]/wl:url}"><xsl:value-of select="wl:page[1]/wl:name"/> Wishlist on Amazon.co.uk</a></p>
                </header>
                <ol class="items">
                    <xsl:apply-templates select="wl:page/wl:item">
                        <xsl:sort select="wl:priority/@level" order="descending"/>
                    </xsl:apply-templates>
                </ol>
                <footer>
                    <p>Based on the <a href="https://vynar.com/2013/amazon-wishlist">Amazon Wishlist API.</a> Written in PHP5, DOM, XSLT 1.0, CSS, SQLite.</p>
                </footer>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="wl:item">
        <li>
            <a href="{wl:link}">
            <h2><xsl:value-of select="wl:title"/></h2>
            <img src="{wl:image/@wl:src}"/>
            <xsl:if test="wl:price">
                <h3><xsl:value-of select="wl:price"/></h3>
            </xsl:if>
            </a>
        </li>
    </xsl:template>
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates/>>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>