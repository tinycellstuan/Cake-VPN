
package org.strongswan.android.logic.imc.attributes;

import java.util.LinkedList;

import org.strongswan.android.logic.imc.collectors.Protocol;
import org.strongswan.android.utils.BufferedByteWriter;

import android.util.Pair;


public class PortFilterAttribute implements Attribute
{
	private final LinkedList<Pair<Protocol, Short>> mPorts = new LinkedList<Pair<Protocol, Short>>();

	public void addPort(Protocol protocol, short port)
	{
		mPorts.add(new Pair<Protocol, Short>(protocol, port));
	}

	@Override
	public byte[] getEncoding()
	{
		BufferedByteWriter writer = new BufferedByteWriter();
		for (Pair<Protocol, Short> port : mPorts)
		{
			writer.put((byte)0);
			writer.put(port.first.getValue());
			writer.put16(port.second);
		}
		return writer.toByteArray();
	}
}
