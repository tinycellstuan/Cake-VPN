
package org.strongswan.android.logic.imc.attributes;

public enum AttributeType
{
	IETF_TESTING(PrivateEnterpriseNumber.IETF, 0),
	IETF_ATTRIBUTE_REQUEST(PrivateEnterpriseNumber.IETF, 1),
	IETF_PRODUCT_INFORMATION(PrivateEnterpriseNumber.IETF, 2),
	IETF_NUMERIC_VERSION(PrivateEnterpriseNumber.IETF, 3),
	IETF_STRING_VERSION(PrivateEnterpriseNumber.IETF, 4),
	IETF_OPERATIONAL_STATUS(PrivateEnterpriseNumber.IETF, 5),
	IETF_PORT_FILTER(PrivateEnterpriseNumber.IETF, 6),
	IETF_INSTALLED_PACKAGES(PrivateEnterpriseNumber.IETF, 7),
	IETF_PA_TNC_ERROR(PrivateEnterpriseNumber.IETF, 8),
	IETF_ASSESSMENT_RESULT(PrivateEnterpriseNumber.IETF, 9),
	IETF_REMEDIATION_INSTRUCTIONS(PrivateEnterpriseNumber.IETF, 10),
	IETF_FORWARDING_ENABLED(PrivateEnterpriseNumber.IETF, 11),
	IETF_FACTORY_DEFAULT_PWD_ENABLED(PrivateEnterpriseNumber.IETF, 12),
	IETF_RESERVED(PrivateEnterpriseNumber.IETF, 0xffffffff),
	ITA_SETTINGS(PrivateEnterpriseNumber.ITA, 4),
	ITA_DEVICE_ID(PrivateEnterpriseNumber.ITA, 8);

	private PrivateEnterpriseNumber mVendor;
	private int mType;

		private AttributeType(PrivateEnterpriseNumber vendor, int type)
	{
		mVendor = vendor;
		mType = type;
	}

		public PrivateEnterpriseNumber getVendor()
	{
		return mVendor;
	}

	public int getType()
	{
		return mType;
	}

		public static AttributeType fromValues(int vendor, int type)
	{
		PrivateEnterpriseNumber pen = PrivateEnterpriseNumber.fromValue(vendor);

		if (pen == null)
		{
			return null;
		}
		for (AttributeType attr : AttributeType.values())
		{
			if (attr.mVendor == pen && attr.mType == type)
			{
				return attr;
			}
		}
		return null;
	}
}
