
package org.strongswan.android.logic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import androidx.core.content.ContextCompat;
import com.one.hotspot.vpn.free.master.manager.VpnManager;
import com.one.hotspot.vpn.free.master.util.Logger;
import com.one.hotspot.vpn.free.master.R;

import org.strongswan.android.data.VpnProfile;
import org.strongswan.android.data.VpnProfileDataSource;
import org.strongswan.android.logic.imc.ImcState;
import org.strongswan.android.logic.imc.RemediationInstruction;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class VpnStateService extends Service {
    private static final String TAG = "VpnStateService";
    private static long RETRY_INTERVAL = 1000;
    private static long MAX_RETRY_INTERVAL = 120000;
    private static int RETRY_MSG = 1;
    private final HashSet<VpnStateListener> mListeners = new HashSet<VpnStateListener>();
    private final IBinder mBinder = new LocalBinder();
    private final LinkedList<RemediationInstruction> mRemediationInstructions = new LinkedList<RemediationInstruction>();
    private long mConnectionID = 0;
    private Handler mHandler;
    private VpnProfile mProfile;
    private State mState = State.DISABLED;
    private ErrorState mError = ErrorState.NO_ERROR;
    private ImcState mImcState = ImcState.UNKNOWN;
    private RetryTimeoutProvider mTimeoutProvider = new RetryTimeoutProvider();
    private long mRetryTimeout;
    private long mRetryIn;
    private VpnManager.OnVpnStateChange onVpnStateChange;

    @Override
    public void onCreate() {

        mHandler = new RetryHandler(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
    }

       public void registerListener(VpnStateListener listener) {
        mListeners.add(listener);
    }

      public void unregisterListener(VpnStateListener listener) {
        mListeners.remove(listener);
    }

     public VpnProfile getProfile() {
        return mProfile;
    }

       public long getConnectionID() {
        return mConnectionID;
    }

      public int getRetryTimeout() {
        return (int) (mRetryTimeout / 1000);
    }

   public int getRetryIn() {
        return (int) (mRetryIn / 1000);
    }

    public State getState() {
        return mState;
    }

     public void setState(final State state) {
        notifyListeners(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (state == State.CONNECTED) {
                    mTimeoutProvider.reset();
                }
                if (VpnStateService.this.mState != state) {
                    VpnStateService.this.mState = state;
                    return true;
                }
                return false;
            }
        });
    }

     public ErrorState getErrorState() {
        return mError;
    }

    public int getErrorText() {
        switch (mError) {
            case AUTH_FAILED:
                if (mImcState == ImcState.BLOCK) {
                    return R.string.error_assessment_failed;
                } else {
                    return R.string.error_auth_failed;
                }
            case PEER_AUTH_FAILED:
                return R.string.error_peer_auth_failed;
            case LOOKUP_FAILED:
                return R.string.error_lookup_failed;
            case UNREACHABLE:
                return R.string.error_unreachable;
            case PASSWORD_MISSING:
                return R.string.error_password_missing;
            case CERTIFICATE_UNAVAILABLE:
                return R.string.error_certificate_unavailable;
            default:
                return R.string.error_generic;
        }
    }

     public ImcState getImcState() {
        return mImcState;
    }

    public void setImcState(final ImcState state) {
        notifyListeners(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (state == ImcState.UNKNOWN) {
                    VpnStateService.this.mRemediationInstructions.clear();
                }
                if (VpnStateService.this.mImcState != state) {
                    VpnStateService.this.mImcState = state;
                    return true;
                }
                return false;
            }
        });
    }

     public List<RemediationInstruction> getRemediationInstructions() {
        return Collections.unmodifiableList(mRemediationInstructions);
    }

    public void disconnect() {
        resetRetryTimer();
        setError(ErrorState.NO_ERROR);
     Context context = getApplicationContext();
        Intent intent = new Intent(context, CharonVpnService.class);
        intent.setAction(CharonVpnService.DISCONNECT_ACTION);
        context.startService(intent);
    }

      public void connect(Bundle profileInfo, boolean fromScratch) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, CharonVpnService.class);
        if (profileInfo == null) {
            profileInfo = new Bundle();
            profileInfo.putString(VpnProfileDataSource.KEY_UUID, mProfile.getUUID().toString());
            profileInfo.putString(VpnProfileDataSource.KEY_PASSWORD, mProfile.getPassword());
        }
        if (fromScratch) {
             mTimeoutProvider.reset();
        } else {
            profileInfo.putBoolean(CharonVpnService.KEY_IS_RETRY, true);
        }
        intent.putExtras(profileInfo);
        ContextCompat.startForegroundService(context, intent);
    }


    private void notifyListeners(final Callable<Boolean> change) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (change.call()) {
                        for (VpnStateListener listener : mListeners) {
                            listener.stateChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void startConnection(final VpnProfile profile) {
        notifyListeners(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                resetRetryTimer();
                VpnStateService.this.mConnectionID++;
                VpnStateService.this.mProfile = profile;
                VpnStateService.this.mState = State.CONNECTING;
                VpnStateService.this.mError = ErrorState.NO_ERROR;
                VpnStateService.this.mImcState = ImcState.UNKNOWN;
                VpnStateService.this.mRemediationInstructions.clear();
                return true;
            }
        });
    }


    public void setError(final ErrorState error) {
        notifyListeners(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (VpnStateService.this.mError != error) {
                    if (VpnStateService.this.mError == ErrorState.NO_ERROR) {
                        setRetryTimer(error);
                    } else if (error == ErrorState.NO_ERROR) {
                        resetRetryTimer();
                    }
                    VpnStateService.this.mError = error;
                    return true;
                }
                return false;
            }
        });
    }


    public void addRemediationInstruction(final RemediationInstruction instruction) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VpnStateService.this.mRemediationInstructions.add(instruction);
            }
        });
    }


    private void setRetryTimer(ErrorState error) {
        mRetryTimeout = mRetryIn = mTimeoutProvider.getTimeout(error);
        if (mRetryTimeout <= 0) {
            return;
        }
        mHandler.sendMessageAtTime(mHandler.obtainMessage(RETRY_MSG), SystemClock.uptimeMillis() + RETRY_INTERVAL);
    }

    private void resetRetryTimer() {
        mRetryTimeout = 0;
        mRetryIn = 0;
    }

    public void setOnVpnChangeListener(VpnManager.OnVpnStateChange onVpnStateChange) {
        registerListener(new VpnStateListener() {
            @Override
            public void stateChanged() {
                if (onVpnStateChange != null) {
                    switch (getState()) {
                        case DISABLED:
                            Logger.INSTANCE.d(TAG, "Disabled");
                            onVpnStateChange.onStateChange(VpnManager.VpnState.DISCONNECTED);
                            break;
                        case CONNECTED:
                            Logger.INSTANCE.d(TAG, "Connected");
                            onVpnStateChange.onStateChange(VpnManager.VpnState.CONNECTED);
                            break;
                        case CONNECTING:
                            Logger.INSTANCE.d(TAG, "Connecting");
                            onVpnStateChange.onStateChange(VpnManager.VpnState.CONNECTING);
                            break;
                        case DISCONNECTING:
                            Logger.INSTANCE.d(TAG, "Disconnecting");
                            onVpnStateChange.onStateChange(VpnManager.VpnState.DISCONNECTING);
                            break;
                    }
                }
            }
        });
    }

    public enum State {
        DISABLED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
    }

    public enum ErrorState {
        NO_ERROR,
        AUTH_FAILED,
        PEER_AUTH_FAILED,
        LOOKUP_FAILED,
        UNREACHABLE,
        GENERIC_ERROR,
        PASSWORD_MISSING,
        CERTIFICATE_UNAVAILABLE,
    }

    public interface VpnStateListener {
        void stateChanged();
    }


    private static class RetryHandler extends Handler {
        WeakReference<VpnStateService> mService;

        public RetryHandler(VpnStateService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {

            if (mService.get().mRetryTimeout <= 0) {
                return;
            }
            mService.get().mRetryIn -= RETRY_INTERVAL;
            if (mService.get().mRetryIn > 0) {
                long next = SystemClock.uptimeMillis() + RETRY_INTERVAL;

                for (VpnStateListener listener : mService.get().mListeners) {
                    listener.stateChanged();
                }
                sendMessageAtTime(obtainMessage(RETRY_MSG), next);
            } else {
                mService.get().connect(null, false);
            }
        }
    }


    private static class RetryTimeoutProvider {
        private long mRetry;

        private long getBaseTimeout(ErrorState error) {
            switch (error) {
                case AUTH_FAILED:
                    return 10000;
                case PEER_AUTH_FAILED:
                    return 5000;
                case LOOKUP_FAILED:
                    return 5000;
                case UNREACHABLE:
                    return 5000;
                case PASSWORD_MISSING:

                    return 0;
                case CERTIFICATE_UNAVAILABLE:

                    return 5000;
                default:
                    return 10000;
            }
        }


        public long getTimeout(ErrorState error) {
            long timeout = (long) (getBaseTimeout(error) * Math.pow(2, mRetry++));
            return Math.min((timeout / 1000) * 1000, MAX_RETRY_INTERVAL);
        }


        public void reset() {
            mRetry = 0;
        }
    }


    public class LocalBinder extends Binder {
        public VpnStateService getService() {
            return VpnStateService.this;
        }
    }
}
