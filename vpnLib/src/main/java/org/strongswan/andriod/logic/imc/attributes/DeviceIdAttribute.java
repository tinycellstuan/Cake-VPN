
package org.strongswan.android.logic.imc.attributes;


public class DeviceIdAttribute implements Attribute
{
	private String mDeviceId;

	public void setDeviceId(String deviceId)
	{
		this.mDeviceId = deviceId;
	}

	@Override
	public byte[] getEncoding()
	{
		return mDeviceId.getBytes();
	}
}
