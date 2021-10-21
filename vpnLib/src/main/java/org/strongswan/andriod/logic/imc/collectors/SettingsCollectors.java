
package org.strongswan.android.logic.imc.collectors;

import java.util.Locale;

import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.SettingsAttribute;

import android.content.ContentResolver;
import android.content.Context;

public class SettingsCollector implements Collector
{
	private final ContentResolver mContentResolver;
	private final String[] mSettings;

	public SettingsCollector(Context context, String[] args)
	{
		mContentResolver = context.getContentResolver();
		mSettings = args;
	}

	@Override
	public Attribute getMeasurement()
	{
		if (mSettings == null || mSettings.length == 0)
		{
			return null;
		}
		SettingsAttribute attribute = new SettingsAttribute();
		for (String name : mSettings)
		{
			String value = android.provider.Settings.Secure.getString(mContentResolver, name.toLowerCase(Locale.US));
			if (value == null)
			{
				value = android.provider.Settings.System.getString(mContentResolver, name.toLowerCase(Locale.US));
			}
			if (value != null)
			{
				attribute.addSetting(name, value);
			}
		}
		return attribute;
	}
}
