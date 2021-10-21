
package org.strongswan.android.logic.imc.attributes;

import org.strongswan.android.utils.BufferedByteWriter;


public class StringVersionAttribute implements Attribute
{
	private String mVersionNumber;
	private String mBuildNumber;

	public void setProductVersionNumber(String version)
	{
		this.mVersionNumber = version;
	}


	public void setInternalBuildNumber(String build)
	{
		this.mBuildNumber = build;
	}

	@Override
	public byte[] getEncoding()
	{
		BufferedByteWriter writer = new BufferedByteWriter();
		writer.putLen8(mVersionNumber.getBytes());
		writer.putLen8(mBuildNumber.getBytes());
		writer.put((byte)0);
		return writer.toByteArray();
	}
}
