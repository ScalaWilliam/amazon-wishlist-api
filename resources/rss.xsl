<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:wi="https://vynar.com/2013/amazon-wishlist"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:xhtml="http://www.w3.org/1999/xhtml">
    <xsl:output indent="yes"/>
    <xsl:template match="wi:wishlist"> 
        
        <rss version="2.0">
            <channel>
                <title>My wishlist, ID <xsl:value-of select="wi:id"/></title>
                <description>This is an example of an RSS feed</description>
                <link><xsl:value-of select="wi:url"/></link>
                <atom:link rel="self" href="http://example.net/"/>
                <lastBuildDate><xsl:value-of select="(wi:page/wi:item/wi:added/@wi:rss)[1]"/></lastBuildDate>
            
                <ttl>1800</ttl>
                <xsl:apply-templates select="wi:page/wi:item"/>
            </channel>
        </rss>
        
    </xsl:template>
    <xsl:template match="wi:item">
        <item>
            <title><xsl:value-of select="wi:title"/></title>
            <link><xsl:value-of select="/wi:wishlist/wi:url"/></link>
            <guid><xsl:value-of select="wi:link"/></guid>
            <pubDate><xsl:value-of select="wi:added/@wi:rss"/></pubDate>
            <description>
                <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
                <div xmlns="http://www.w3.org/1999/xhtml">
                    <p>I added <a href="{wi:link}"><xsl:value-of select="wi:title"/></a>
                        to <a href="{/wi:wishlist/wi:url}">my Amazon Wishlist</a>.</p>
                    <p>Priority: <xsl:call-template name="priority">
                        <xsl:with-param name="level" select="wi:priority/@wi:level"/>
                    </xsl:call-template></p>
                </div>
                <xsl:text disable-output-escaping="yes">]]</xsl:text>
                <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
            </description>
        </item>
    </xsl:template>
    
    <xsl:template name="priority">
        <xsl:param name="level" select="3"/>
        <xsl:choose>
            <xsl:when test="$level = 5">highest</xsl:when>
            <xsl:when test="$level = 4">high</xsl:when>
            <xsl:when test="$level = 3">medium</xsl:when>
            <xsl:when test="$level = 2">low</xsl:when>
            <xsl:when test="$level = 1">lowest</xsl:when>
            <xsl:otherwise>unknown</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>