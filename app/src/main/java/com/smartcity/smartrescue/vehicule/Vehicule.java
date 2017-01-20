package com.smartcity.smartrescue.vehicule;

import android.content.SharedPreferences;

public class Vehicule {
    private Vehicule() {}

    private static class SingletonHolder
    {
        private final static Vehicule instance = new Vehicule();
        private static SharedPreferences sp;
        private static Integer idEmergency;
    }

    public static Vehicule getInstance()
    {
        return SingletonHolder.instance;
    }

    public Status getVehiculeStatus() {
        String status = SingletonHolder.sp.getString("status", "");
        if (status.isEmpty()) {
            throw new NullPointerException("Status empty");
        }
        return Status.valueOf(status);
    }

    public boolean changeVehiculeStatus(Status status) {
        return SingletonHolder.sp.edit().putString("status", status.name()).commit();
    }

    public void setSharedPreferences(SharedPreferences sp) {
        SingletonHolder.sp = sp;
    }

    public Integer getIdEmergency() {
        return SingletonHolder.idEmergency;
    }

    public void setIdEmergency(Integer idEmergency) {
        SingletonHolder.idEmergency = idEmergency;
    }
}
