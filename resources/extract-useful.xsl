<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:wi="https://vynar.com/2013/amazon-wishlist">
    <xsl:output indent="yes"/>
    
    <xsl:template match="wi:*">
        <xsl:copy>
            <xsl:apply-templates select="attribute::*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="wi:page">
        <xsl:copy>
            <xsl:apply-templates select="attribute::*"/>
            <!--
            <xsl:apply-templates select=".//wi:*[not(./ancestor::wi:item)]"/>
            <xsl:apply-templates select=".//wi:item"/>
                <xsl:sort order="ascending" select="(.//wi:added/@wi:unix)[1]"/>
            </xsl:apply-templates>
            -->
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="wi:wishlist">
        <xsl:copy>
            <xsl:apply-templates select="attribute::*"/>
            <xsl:copy-of select="descendant::wi:page[1]/wi:url"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="attribute::wi:*">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="attribute::*[parent::wi:*]">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="wi:item/text() | wi:page/text()"/>

    <xsl:template match="wi:*/text()">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="node()">
        <xsl:apply-templates/>        
    </xsl:template>
</xsl:stylesheet>