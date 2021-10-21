
package org.strongswan.android.logic.imc.attributes;

import org.strongswan.android.utils.BufferedByteWriter;

public class ProductInformationAttribute implements Attribute
{
	private final String PRODUCT_NAME = "Android";
	private final short PRODUCT_ID = 0;

	@Override
	public byte[] getEncoding()
	{
		BufferedByteWriter writer = new BufferedByteWriter();
		writer.put24(PrivateEnterpriseNumber.GOOGLE.getValue());
		writer.put16(PRODUCT_ID);
		writer.put(PRODUCT_NAME.getBytes());
		return writer.toByteArray();
	}
}
