
package org.strongswan.android.data;

import java.util.EnumSet;

public enum VpnType
{
	IKEV2_EAP("ikev2-eap", EnumSet.of(VpnTypeFeature.USER_PASS)),
	IKEV2_CERT("ikev2-cert", EnumSet.of(VpnTypeFeature.CERTIFICATE)),
	IKEV2_CERT_EAP("ikev2-cert-eap", EnumSet.of(VpnTypeFeature.USER_PASS, VpnTypeFeature.CERTIFICATE)),
	IKEV2_EAP_TLS("ikev2-eap-tls", EnumSet.of(VpnTypeFeature.CERTIFICATE)),
	IKEV2_BYOD_EAP("ikev2-byod-eap", EnumSet.of(VpnTypeFeature.USER_PASS, VpnTypeFeature.BYOD));


	public enum VpnTypeFeature
	{
		CERTIFICATE,
		USER_PASS,
		BYOD;
	}

	private String mIdentifier;
	private EnumSet<VpnTypeFeature> mFeatures;


	VpnType(String id, EnumSet<VpnTypeFeature> features)
	{
		mIdentifier = id;
		mFeatures = features;
	}


	public String getIdentifier()
	{
		return mIdentifier;
	}


	public boolean has(VpnTypeFeature feature)
	{
		return mFeatures.contains(feature);
	}


	public static VpnType fromIdentifier(String identifier)
	{
		for (VpnType type : VpnType.values())
		{
			if (identifier.equals(type.mIdentifier))
			{
				return type;
			}
		}
		return VpnType.IKEV2_EAP;
	}
}
