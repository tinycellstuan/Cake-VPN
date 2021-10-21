
package org.strongswan.android.logic.imc.collectors;

import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.StringVersionAttribute;

public class StringVersionCollector implements Collector
{
	@Override
	public Attribute getMeasurement()
	{
		StringVersionAttribute attribute = new StringVersionAttribute();
		attribute.setProductVersionNumber(android.os.Build.VERSION.RELEASE);
		attribute.setInternalBuildNumber(android.os.Build.DISPLAY);
		return attribute;
	}
}
