<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:wi="https://vynar.com/2013/amazon-wishlist"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/2005/Atom">
    <xsl:output indent="yes"/>
    <xsl:template match="wi:wishlist"> 
        <feed>
            <title>My wishlist, ID <xsl:value-of select="wi:id"/></title>
            <link href="{wi:url}"/>
            <link href="{wi:url}" rel="self"/>
            <id><xsl:value-of select="wi:url"/></id>
            <updated><xsl:value-of select="(wi:page/wi:item/wi:added/@wi:atom)[1]"/></updated>
            <xsl:apply-templates select="wi:page/wi:item"/>
        </feed>
        
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
    <xsl:template match="wi:item">
        <entry>
            <title><xsl:value-of select="wi:title"/></title>
            <link href="{/wi:wishlist/wi:url}"/>
            <id><xsl:value-of select="wi:link"/></id>
            <updated><xsl:value-of select="wi:added/@wi:atom"/></updated>
            <summary type="xhtml">
                <div xmlns="http://www.w3.org/1999/xhtml">
                    <p>I added <a href="{wi:link}"><xsl:value-of select="wi:title"/></a>
                    to <a href="{/wi:wishlist/wi:url}">my Amazon Wishlist</a>.</p>
                    <p>Priority: <xsl:call-template name="priority">
                        <xsl:with-param name="level" select="wi:priority/@wi:level"/>
                    </xsl:call-template></p>
                </div>
            </summary>
            <author>
                <name>John Smith</name>
                <email>exmple@example.net</email>
            </author>
        </entry>
    </xsl:template>
    
</xsl:stylesheet>