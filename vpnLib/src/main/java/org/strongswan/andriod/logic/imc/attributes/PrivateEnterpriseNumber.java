
package org.strongswan.android.logic.imc.attributes;

public enum PrivateEnterpriseNumber
{
	IETF(0x000000),
	GOOGLE(0x002B79),
	ITA(0x00902a),
	UNASSIGNED(0xfffffe),
	RESERVED(0xffffff);

	private int mValue;

	private PrivateEnterpriseNumber(int value)
	{
		mValue = value;
	}


	public int getValue()
	{
		return mValue;
	}


	public static PrivateEnterpriseNumber fromValue(int value)
	{
		for (PrivateEnterpriseNumber pen : PrivateEnterpriseNumber.values())
		{
			if (pen.mValue == value)
			{
				return pen;
			}
		}
		return null;
	}
}
