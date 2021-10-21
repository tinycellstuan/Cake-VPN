
package org.strongswan.android.logic.imc.collectors;

import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.ProductInformationAttribute;

public class ProductInformationCollector implements Collector
{
	@Override
	public Attribute getMeasurement()
	{
		return new ProductInformationAttribute();
	}
}
