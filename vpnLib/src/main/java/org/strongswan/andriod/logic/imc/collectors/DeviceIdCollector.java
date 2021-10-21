
package org.strongswan.android.logic.imc.collectors;

import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.DeviceIdAttribute;

import android.content.ContentResolver;
import android.content.Context;

public class DeviceIdCollector implements Collector
{
	private final ContentResolver mContentResolver;

	public DeviceIdCollector(Context context)
	{
		mContentResolver = context.getContentResolver();
	}

	@Override
	public Attribute getMeasurement()
	{
		String id = android.provider.Settings.Secure.getString(mContentResolver, "android_id");
		if (id != null)
		{
			DeviceIdAttribute attribute = new DeviceIdAttribute();
			attribute.setDeviceId(id);
			return attribute;
		}
		return null;
	}
}
