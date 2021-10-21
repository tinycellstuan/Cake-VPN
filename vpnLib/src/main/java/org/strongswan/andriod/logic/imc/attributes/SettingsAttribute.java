
package org.strongswan.android.logic.imc.attributes;

import java.util.LinkedList;

import org.strongswan.android.utils.BufferedByteWriter;

import android.util.Pair;


public class SettingsAttribute implements Attribute
{
	private final LinkedList<Pair<String, String>> mSettings = new LinkedList<Pair<String, String>>();

	public void addSetting(String name, String value)
	{
		mSettings.add(new Pair<String, String>(name, value));
	}

	@Override
	public byte[] getEncoding()
	{
		BufferedByteWriter writer = new BufferedByteWriter();
		writer.put32(mSettings.size());
		for (Pair<String, String> pair : mSettings)
		{
			writer.putLen16(pair.first.getBytes());
			writer.putLen16(pair.second.getBytes());
		}
		return writer.toByteArray();
	}
}
