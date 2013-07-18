<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:wl="urn:vynar:wishlist" xmlns="http://www.w3.org/1999/xhtml">
    <xsl:template match="wl:items">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <html>
            <head>
                <title>My Wishlist</title>
                <link href="xhtml-wishlist.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
                <ol class="items">
                    <xsl:apply-templates select="wl:item">
                        <xsl:sort select="wl:priority/@level" order="descending"/>
                    </xsl:apply-templates>
                </ol>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="wl:item">
        <li>
            <a href="{wl:link}">
            <h2><xsl:value-of select="wl:title"/></h2>
            <img src="{wl:image/wl:src}"/>
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