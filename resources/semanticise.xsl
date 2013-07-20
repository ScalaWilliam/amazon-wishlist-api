<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wi="https://vynar.com/2013/amazon-wishlist"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    
    version="1.0">
    
    <xsl:template name="sort_date">
        <xsl:choose xmlns:php="http://php.net/xsl">
            <xsl:when test="function-available('php:function') and php:function('function_exists', '\Awl\aidate')">
                <xsl:copy-of select="php:function('\Awl\aidate',.)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="extract_id">
        <xsl:param name="link"/>
        <xsl:value-of select="substring-before(substring-after($link, 'dp/'), '/')"/>
    </xsl:template>
    
    <xsl:template name="surround">
        <xsl:param name="with"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:element name="wi:{$with}">
                <xsl:apply-templates select="node()"/>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
    
    
    
    <xsl:template match="xhtml:tbody[@class='itemWrapper']">
        <wi:item>
            <xsl:copy>
                <xsl:apply-templates mode="item"/>
            </xsl:copy>
            
            <xsl:variable name="priority" select=".//*[@class='priorityValueText']"/>
            
            <xsl:variable name="level">
                <xsl:choose>
                    <xsl:when test="$priority = 'highest'">5</xsl:when>
                    <xsl:when test="$priority = 'high'">4</xsl:when>
                    <xsl:when test="$priority = 'medium'">3</xsl:when>
                    <xsl:when test="$priority = 'low'">2</xsl:when>
                    <xsl:when test="$priority = 'lowest'">1</xsl:when>
                    <xsl:otherwise>3</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            
            
            <wi:name><xsl:value-of select="@name"/></wi:name>
            <xsl:variable name="link">
                <xsl:value-of select="(.//*[@class='small productTitle']/*/xhtml:a/@href)[1]"/>
            </xsl:variable>
            <wi:link>
                <xsl:value-of select="$link"/>
            </wi:link>
            <wi:id>
                <xsl:call-template name="extract_id">
                    <xsl:with-param name="link" select="$link"/>
                </xsl:call-template>
            </wi:id>
            <wi:priority wi:level="{$level}">
                <xsl:value-of select="$priority"/>
            </wi:priority>
        </wi:item>
    </xsl:template>
    
    <xsl:template mode="item" match="*[@class='small productTitle']/*/xhtml:a[@href]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <wi:title>
                <xsl:apply-templates select="node()"/>
            </wi:title>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template mode="item" match="*[@class='productImage']/*/xhtml:img[1]">
        <wi:image wi:width="{@width}" wi:height="{@height}" wi:src="{@src}"/>
        <xsl:copy>
            <xsl:apply-templates mode="item"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template mode="item" match="*[@class='wlPriceBold']/*[1]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <wi:price>
                <xsl:apply-templates select="node()"/>
            </wi:price>
        </xsl:copy>
    </xsl:template>
    <xsl:template mode="item" match="xhtml:td[contains(@class, 'lineItemOwnerInfoJS')]//xhtml:nobr">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:call-template name="sort_date">
                <xsl:with-param name="value">
                    <xsl:value-of select="."/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[@id='profile-description-visitor-Field']/xhtml:span[normalize-space(.) !='' and normalize-space(.)!='Nothing entered']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <wi:about>
                <xsl:value-of select="normalize-space(.)"/>
            </wi:about>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[@id='profile-birthday-Field'][normalize-space(.) != 'None entered' and normalize-space(.) != '']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <wi:birthday>
                <xsl:value-of select="."/>
            </wi:birthday>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[@id='profile-address-Field'][normalize-space(.) != 'None entered' and normalize-space(.) != '']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <wi:address>
                <xsl:apply-templates select="node()"/>
            </wi:address>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[@id='profile-name-Field'][normalize-space(.) != 'None entered' and normalize-space(.) != '']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <wi:name>
                <xsl:apply-templates select="node()"/>
            </wi:name>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="xhtml:h1[@class='visitor']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <wi:title>
                <xsl:apply-templates select="node()"/>
            </wi:title>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="node()|@*" mode="item">
        <xsl:copy>
            <xsl:apply-templates mode="item"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>