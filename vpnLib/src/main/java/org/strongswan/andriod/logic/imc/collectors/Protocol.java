
package org.strongswan.android.logic.imc.collectors;

public enum Protocol
{
	TCP((byte)6, "tcp", "tcp6"),
	UDP((byte)17, "udp", "udp6");

	private final byte mValue;
	private String[] mNames;

	private Protocol(byte value, String... names)
	{
		mValue = value;
		mNames = names;
	}

	public byte getValue()
	{
		return mValue;
	}

	public static Protocol fromName(String name)
	{
		for (Protocol protocol : Protocol.values())
		{
			for (String keyword : protocol.mNames)
			{
				if (keyword.equalsIgnoreCase(name))
				{
					return protocol;
				}
			}
		}
		return null;
	}
}
