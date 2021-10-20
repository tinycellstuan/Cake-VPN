package org.strongswan.android.logic.imc;

public enum ImcState
{
	UNKNOWN(0),
	ALLOW(1),
	BLOCK(2),
	ISOLATE(3);

	private final int mValue;

	private ImcState(int value)
	{
		mValue = value;
	}

	public int getValue()
	{
		return mValue;
	}

		public static ImcState fromValue(int value)
	{
		for (ImcState state : ImcState.values())
		{
			if (state.mValue == value)
			{
				return state;
			}
		}
		return null;
	}
}
