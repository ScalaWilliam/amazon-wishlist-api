<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0">

    <xsl:output indent="yes"/>

    <xsl:variable name="feed-limit">10</xsl:variable>

    <!-- Change the URL of this wishlist RSS -->
    <xsl:template name="self-link">
        <link xmlns="http://www.w3.org/2005/Atom" rel="canonical" href="http://example.net/atom-wishlist.html" type="text/html"/>
        <link xmlns="http://www.w3.org/2005/Atom" rel="self" href="http://example.net/atom-wishlist.xml" type="application/atom+xml"/>
    </xsl:template>

    <!-- Set up your details -->
    <xsl:template name="author">
        <author xmlns="http://www.w3.org/2005/Atom">
            <name xmlns="http://www.w3.org/2005/Atom">Sample user</name>
        </author>
    </xsl:template>

    <xsl:template match="wishlist">
        <feed xmlns="http://www.w3.org/2005/Atom">
            <title xmlns="http://www.w3.org/2005/Atom">My wishlist</title>
            <xsl:call-template name="self-link"/>
            <xsl:apply-templates select="link"/>
            <xsl:apply-templates select="updated"/>
            <xsl:apply-templates select="item[position() &lt;= $feed-limit]">
                <xsl:sort select="added" order="descending"/>
            </xsl:apply-templates>
        </feed>
    </xsl:template>

    <xsl:template match="link">
        <id xmlns="http://www.w3.org/2005/Atom"><xsl:value-of select="."/></id>
        <link xmlns="http://www.w3.org/2005/Atom" href="{string(.)}"/>
    </xsl:template>

    <xsl:template match="name">
        <title><xsl:value-of select="."/></title>
    </xsl:template>

    <xsl:template match="item">
        <entry xmlns="http://www.w3.org/2005/Atom">
            <id xmlns="http://www.w3.org/2005/Atom"><xsl:value-of select="reserve-link"/></id>
            <link xmlns="http://www.w3.org/2005/Atom" href="{string(reserve-link)}"/>
            <xsl:apply-templates select="name"/>
            <xsl:apply-templates select="added"/>
            <xsl:call-template name="author"/>
            <summary xmlns="http://www.w3.org/2005/Atom" type="xhtml"><div xmlns="http://www.w3.org/1999/xhtml" xml:space="preserve">
                <p xmlns="http://www.w3.org/1999/xhtml">I added <a xmlns="http://www.w3.org/1999/xhtml" href="{link}"><xsl:value-of select="name"/></a> to my Amazon Wishlist.</p>
                <xsl:apply-templates select="reserve-link"/>
                <xsl:apply-templates select="reviews-link"/>
            </div></summary>
        </entry>
    </xsl:template>

    <xsl:template match="updated">
        <updated xmlns="http://www.w3.org/2005/Atom"><xsl:value-of select="."/></updated>
    </xsl:template>

    <xsl:template match="name">
        <title xmlns="http://www.w3.org/2005/Atom"><xsl:value-of select="."/></title>
    </xsl:template>

    <xsl:template match="added">
        <updated xmlns="http://www.w3.org/2005/Atom"><xsl:value-of select="."/></updated>
    </xsl:template>

    <xsl:template match="reserve-link">
        <p xmlns="http://www.w3.org/1999/xhtml"><a xmlns="http://www.w3.org/1999/xhtml" href="{string(.)}">Reserve item</a></p>
    </xsl:template>

    <xsl:template match="reviews-link">
        <p xmlns="http://www.w3.org/1999/xhtml"><a xmlns="http://www.w3.org/1999/xhtml" href="{string(.)}">Read reviews</a></p>
    </xsl:template>

</xsl:stylesheet>