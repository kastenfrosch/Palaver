package de.uni_due.paluno.se.palaver.utils;

import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MagicNetworkCallback extends ConnectivityManager.NetworkCallback {
    private boolean connected = false;
    private List<NetworkStateChangeListener> callbacks;

    private static MagicNetworkCallback instance;

    public static MagicNetworkCallback getInstance() {
        if(instance == null) {
            instance = new MagicNetworkCallback();
        }
        return instance;
    }

    private MagicNetworkCallback() {
        callbacks = new ArrayList<>();
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.d("Palaver*****", "network available");
        if(!this.connected) {
            this.connected = true;
            onStateChanged();
        }
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.d("Palaver*****", "network lost");
        if(this.connected) {
            this.connected = false;
            onStateChanged();
        }

    }

    public void addCallback(NetworkStateChangeListener c) {
        this.callbacks.add(c);
    }
    public void removeCallback(NetworkStateChangeListener c) {
        this.callbacks.remove(c);
    }

    private void onStateChanged() {
        for(NetworkStateChangeListener c : this.callbacks) {
            if(connected) {
                c.onInternetAvailable();
            } else {
                c.onInternetLost();
            }
        }
    }

    public interface NetworkStateChangeListener {
        void onInternetAvailable();
        void onInternetLost();
    }
}