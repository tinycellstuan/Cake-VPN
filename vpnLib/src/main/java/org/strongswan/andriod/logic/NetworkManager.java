

package org.strongswan.android.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import java.util.LinkedList;

public class NetworkManager extends BroadcastReceiver implements Runnable
{
	private final Context mContext;
	private volatile boolean mRegistered;
	private ConnectivityManager.NetworkCallback mCallback;
	private Thread mEventNotifier;
	private int mConnectedNetworks = 0;
	private LinkedList<Boolean> mEvents = new LinkedList<>();

	public NetworkManager(Context context)
	{
		mContext = context;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		{
			mCallback = new ConnectivityManager.NetworkCallback()
			{
				@Override
				public void onAvailable(Network network)
				{
					synchronized (NetworkManager.this)
					{
						mConnectedNetworks += 1;
						mEvents.addLast(true);
						NetworkManager.this.notifyAll();
					}
				}

				@Override
				public void onLost(Network network)
				{
					synchronized (NetworkManager.this)
					{
						mConnectedNetworks -= 1;
						mEvents.addLast(mConnectedNetworks > 0);
						NetworkManager.this.notifyAll();
					}
				}
			};
		}
	}

	public void Register()
	{
		mEvents.clear();
		mRegistered = true;
		mEventNotifier = new Thread(this);
		mEventNotifier.start();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		{
			ConnectivityManager cm = mContext.getSystemService(ConnectivityManager.class);
			NetworkRequest.Builder builder = new NetworkRequest.Builder();
			cm.registerNetworkCallback(builder.build(), mCallback);
		}
		else
		{
			registerLegacyReceiver();
		}
	}


	private void registerLegacyReceiver()
	{
			mContext.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public void Unregister()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		{
			ConnectivityManager cm = mContext.getSystemService(ConnectivityManager.class);
			cm.unregisterNetworkCallback(mCallback);
		}
		else
		{
			mContext.unregisterReceiver(this);
		}
		mRegistered = false;
		synchronized (this)
		{
			notifyAll();
		}
		try
		{
			mEventNotifier.join();
			mEventNotifier = null;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public boolean isConnected()
	{
		ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = null;
		if (cm != null)
		{
			info = cm.getActiveNetworkInfo();
		}
		return info != null && info.isConnected();
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		synchronized (this)
		{
			mEvents.addLast(isConnected());
			notifyAll();
		}
	}

	@Override
	public void run()
	{
		while (mRegistered)
		{
			boolean connected;

			synchronized (this)
			{
				try
				{
					while (mRegistered && mEvents.isEmpty())
					{
						wait();
					}
				}
				catch (InterruptedException ex)
				{
					break;
				}
				if (!mRegistered)
				{
					break;
				}
				connected = mEvents.removeFirst();
			}
			networkChanged(!connected);
		}
	}

public native void networkChanged(boolean disconnected);
}
