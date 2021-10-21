
package org.strongswan.android.logic.imc.attributes;

import java.util.LinkedList;

import org.strongswan.android.utils.BufferedByteWriter;

import android.util.Pair;


public class InstalledPackagesAttribute implements Attribute
{
	private final short RESERVED = 0;
	private final LinkedList<Pair<String, String>> mPackages = new LinkedList<Pair<String, String>>();


	public void addPackage(String name, String version)
	{
		mPackages.add(new Pair<String, String>(name, version));
	}

	@Override
	public byte[] getEncoding()
	{
		BufferedByteWriter writer = new BufferedByteWriter();
		writer.put16(RESERVED);
		writer.put16((short)mPackages.size());
		for (Pair<String, String> pair : mPackages)
		{
			writer.putLen8(pair.first.getBytes());
			writer.putLen8(pair.second.getBytes());
		}
		return writer.toByteArray();
	}
}
