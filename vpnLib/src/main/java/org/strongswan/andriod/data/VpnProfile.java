
package org.strongswan.android.data;


import android.text.TextUtils;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

public class VpnProfile implements Cloneable
{
public static final int SPLIT_TUNNELING_BLOCK_IPV4 = 1;
	public static final int SPLIT_TUNNELING_BLOCK_IPV6 = 2;

	public static final int FLAGS_SUPPRESS_CERT_REQS = 1 << 0;
	public static final int FLAGS_DISABLE_CRL = 1 << 1;
	public static final int FLAGS_DISABLE_OCSP = 1 << 2;
	public static final int FLAGS_STRICT_REVOCATION = 1 << 3;
	public static final int FLAGS_RSA_PSS = 1 << 4;

	private String mName, mGateway, mUsername, mPassword, mCertificate, mUserCertificate, mCountry;
	private String mRemoteId, mLocalId, mExcludedSubnets, mIncludedSubnets, mSelectedApps;
	private String mIkeProposal, mEspProposal, mDnsServers;
	private Integer mMTU, mPort, mSplitTunneling, mNATKeepAlive, mFlags;
	private SelectedAppsHandling mSelectedAppsHandling = SelectedAppsHandling.SELECTED_APPS_DISABLE;
	private VpnType mVpnType;
	private UUID mUUID;
	private long mId = -1;

	public enum SelectedAppsHandling
	{
		SELECTED_APPS_DISABLE(0),
		SELECTED_APPS_EXCLUDE(1),
		SELECTED_APPS_ONLY(2);

		private Integer mValue;

		SelectedAppsHandling(int value)
		{
			mValue = value;
		}

		public Integer getValue()
		{
			return mValue;
		}
	}

	public VpnProfile()
	{
		this.mUUID = UUID.randomUUID();
	}

	public long getId()
	{
		return mId;
	}

	public void setId(long id)
	{
		this.mId = id;
	}

	public void setUUID(UUID uuid)
	{
		this.mUUID = uuid;
	}

	public UUID getUUID()
	{
		return mUUID;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		this.mName = name;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	public String getGateway()
	{
		return mGateway;
	}

	public void setGateway(String gateway)
	{
		this.mGateway = gateway;
	}

	public VpnType getVpnType()
	{
		return mVpnType;
	}

	public void setVpnType(VpnType type)
	{
		this.mVpnType = type;
	}

	public String getIkeProposal()
	{
		return mIkeProposal;
	}

	public void setIkeProposal(String proposal)
	{
		this.mIkeProposal = proposal;
	}

	public String getEspProposal()
	{
		return mEspProposal;
	}

	public void setEspProposal(String proposal)
	{
		this.mEspProposal = proposal;
	}

	public String getDnsServers()
	{
		return mDnsServers;
	}

	public void setDnsServers(String dns)
	{
		this.mDnsServers = dns;
	}

	public String getUsername()
	{
		return mUsername;
	}

	public void setUsername(String username)
	{
		this.mUsername = username;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public void setPassword(String password)
	{
		this.mPassword = password;
	}

	public String getCertificateAlias()
	{
		return mCertificate;
	}

	public void setCertificateAlias(String alias)
	{
		this.mCertificate = alias;
	}

	public String getUserCertificateAlias()
	{
		return mUserCertificate;
	}

	public void setUserCertificateAlias(String alias)
	{
		this.mUserCertificate = alias;
	}

	public String getLocalId()
	{
		return mLocalId;
	}

	public void setLocalId(String localId)
	{
		this.mLocalId = localId;
	}

	public String getRemoteId()
	{
		return mRemoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.mRemoteId = remoteId;
	}

	public Integer getMTU()
	{
		return mMTU;
	}

	public void setMTU(Integer mtu)
	{
		this.mMTU = mtu;
	}

	public Integer getPort()
	{
		return mPort;
	}

	public void setPort(Integer port)
	{
		this.mPort = port;
	}

	public Integer getNATKeepAlive()
	{
		return mNATKeepAlive;
	}

	public void setNATKeepAlive(Integer keepalive)
	{
		this.mNATKeepAlive = keepalive;
	}

	public void setExcludedSubnets(String excludedSubnets)
	{
		this.mExcludedSubnets = excludedSubnets;
	}

	public String getExcludedSubnets()
	{
		return mExcludedSubnets;
	}

	public void setIncludedSubnets(String includedSubnets)
	{
		this.mIncludedSubnets = includedSubnets;
	}

	public String getIncludedSubnets()
	{
		return mIncludedSubnets;
	}

	public void setSelectedApps(String selectedApps)
	{
		this.mSelectedApps = selectedApps;
	}

	public void setSelectedApps(SortedSet<String> selectedApps)
	{
		this.mSelectedApps = selectedApps.size() > 0 ? TextUtils.join(" ", selectedApps) : null;
	}

	public String getSelectedApps()
	{
		return mSelectedApps;
	}

	public SortedSet<String> getSelectedAppsSet()
	{
		TreeSet<String> set = new TreeSet<>();
		if (!TextUtils.isEmpty(mSelectedApps))
		{
			set.addAll(Arrays.asList(mSelectedApps.split("\\s+")));
		}
		return set;
	}

	public void setSelectedAppsHandling(SelectedAppsHandling selectedAppsHandling)
	{
		this.mSelectedAppsHandling = selectedAppsHandling;
	}

	public void setSelectedAppsHandling(Integer value)
	{
		mSelectedAppsHandling = SelectedAppsHandling.SELECTED_APPS_DISABLE;
		for (SelectedAppsHandling handling : SelectedAppsHandling.values())
		{
			if (handling.mValue.equals(value))
			{
				mSelectedAppsHandling = handling;
				break;
			}
		}
	}

	public SelectedAppsHandling getSelectedAppsHandling()
	{
		return mSelectedAppsHandling;
	}

	public Integer getSplitTunneling()
	{
		return mSplitTunneling;
	}

	public void setSplitTunneling(Integer splitTunneling)
	{
		this.mSplitTunneling = splitTunneling;
	}

	public Integer getFlags()
	{
		return mFlags == null ? 0 : mFlags;
	}

	public void setFlags(Integer flags)
	{
		this.mFlags = flags;
	}

	@Override
	public String toString()
	{
		return mName;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof VpnProfile)
		{
			VpnProfile other = (VpnProfile)o;
			if (this.mUUID != null && other.getUUID() != null)
			{
				return this.mUUID.equals(other.getUUID());
			}
			return this.mId == other.getId();
		}
		return false;
	}

	@Override
	public VpnProfile clone()
	{
		try
		{
			return (VpnProfile)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError();
		}
	}
}
