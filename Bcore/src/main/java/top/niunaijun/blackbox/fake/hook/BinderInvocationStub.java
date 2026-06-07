package top.niunaijun.blackbox.fake.hook;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;
import java.util.Map;

import black.android.os.BRServiceManager;

/**
 * Abstract stub that intercepts binder-level invocations by replacing a system service's
 * IBinder in the ServiceManager cache. Extends {@link ClassInvocationStub} and implements
 * {@link IBinder} to act as a transparent proxy for the original binder, forwarding
 * most binder operations while allowing method-level hooks via the parent class.
 */
public abstract class BinderInvocationStub extends ClassInvocationStub implements IBinder {
    private IBinder mBaseBinder;

    /**
     * Constructs a new BinderInvocationStub wrapping the given base binder.
     *
     * @param baseBinder the original IBinder to wrap
     */
    public BinderInvocationStub(IBinder baseBinder) {
        mBaseBinder = baseBinder;
    }

    @Override
    protected void onBindMethod() {
    }

    @Nullable
    @Override
    public String getInterfaceDescriptor() throws RemoteException {
        return mBaseBinder.getInterfaceDescriptor();
    }

    @Override
    public boolean pingBinder() {
        return mBaseBinder.pingBinder();
    }

    @Override
    public boolean isBinderAlive() {
        return mBaseBinder.isBinderAlive();
    }

    /**
     * Returns the proxy invocation as the local interface, ensuring all calls
     * go through the hook chain.
     *
     * @param descriptor the interface descriptor
     * @return the proxy IInterface
     */
    @Nullable
    @Override
    public IInterface queryLocalInterface(@NonNull String descriptor) {
        return (IInterface) getProxyInvocation();
    }

    @Override
    public void dump(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {
        mBaseBinder.dump(fd, args);
    }

    @Override
    public void dumpAsync(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {
        mBaseBinder.dumpAsync(fd, args);
    }

    @Override
    public boolean transact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        return mBaseBinder.transact(code, data, reply, flags);
    }

    @Override
    public void linkToDeath(@NonNull DeathRecipient recipient, int flags) throws RemoteException {
        mBaseBinder.linkToDeath(recipient, flags);
    }

    @Override
    public boolean unlinkToDeath(@NonNull DeathRecipient recipient, int flags) {
        return mBaseBinder.unlinkToDeath(recipient, flags);
    }


    /**
     * Replaces the named system service in the ServiceManager's cache with this stub.
     *
     * @param name the service name to replace (e.g., "activity", "package")
     */
    protected void replaceSystemService(String name) {
        Map<String, IBinder> services = BRServiceManager.get().sCache();
        services.put(name, this);
    }
}
