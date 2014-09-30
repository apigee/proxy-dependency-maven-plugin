<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="http://schemas.microsoft.com/search/local/ws/rest/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" exclude-result-prefixes="ns0 xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<Locations>
			<xsl:attribute name="count" select="fn:string(ns0:Response/ns0:ResourceSets/ns0:ResourceSet/ns0:EstimatedTotal)"/>
			<xsl:for-each select="ns0:Response/ns0:ResourceSets/ns0:ResourceSet/ns0:Resources/ns0:Location">
				<xsl:variable name="var1_resultof_first" as="node()" select="ns0:BoundingBox"/>
				<xsl:variable name="var2_resultof_first" as="node()" select="ns0:Address"/>
				<xsl:variable name="var3_resultof_first" as="node()" select="ns0:Point"/>
				<Location>
					<Name>
						<xsl:sequence select="fn:string($var1_resultof_first/ns0:Name)"/>
					</Name>
					<BoundingBox>
						<SouthLatitude>
						        <xsl:sequence select="fn:string($var1_resultof_first/ns0:SouthLatitude)"/>
						</SouthLatitude>
						<WestLongitude>
						        <xsl:sequence select="fn:string($var1_resultof_first/ns0:WestLongitude)"/>
						</WestLongitude>
						<NorthLatitude>
						        <xsl:sequence select="fn:string($var1_resultof_first/ns0:NorthLatitude)"/>
						</NorthLatitude>
						<EastLongitude>
						        <xsl:sequence select="fn:string($var1_resultof_first/ns0:EastLongitude)"/>
						</EastLongitude>
					</BoundingBox>
					<Type>
						<xsl:sequence select="fn:string(ns0:EntityType)"/>
					</Type>
					<Address>
						<Latitude>
							<xsl:sequence select="fn:string($var3_resultof_first/ns0:Latitude)"/>
						</Latitude>
						<Longitude>
							<xsl:sequence select="fn:string($var3_resultof_first/ns0:Longitude)"/>
						</Longitude>
						<AddressLine1>
							<xsl:sequence select="fn:string($var2_resultof_first/ns0:AddressLine)"/>
						</AddressLine1>
						<City>
							<xsl:sequence select="fn:string($var2_resultof_first/ns0:Locality)"/>
						</City>
						<Subdivision>
							<xsl:sequence select="fn:string($var2_resultof_first/ns0:AdminDistrict)"/>
						</Subdivision>
						<PostalCode>
							<xsl:sequence select="fn:string($var2_resultof_first/ns0:PostalCode)"/>
						</PostalCode>
						<CountryName>
							<xsl:sequence select="fn:string($var2_resultof_first/ns0:CountryRegion)"/>
						</CountryName>
						<FormattedAddress>
							<xsl:sequence select="fn:string($var2_resultof_first/ns0:FormattedAddress)"/>
						</FormattedAddress>
					</Address>
				</Location>
			</xsl:for-each>
		</Locations>
	</xsl:template>
</xsl:stylesheet>