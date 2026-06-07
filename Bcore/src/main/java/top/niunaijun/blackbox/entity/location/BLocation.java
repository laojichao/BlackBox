/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.niunaijun.blackbox.entity.location;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable representation of a geographic location used for virtual location
 * spoofing within the BlackBox environment.
 * <p>
 * Wraps latitude, longitude, altitude, speed, bearing, and accuracy values,
 * and can convert itself to a standard Android {@link Location} object via
 * {@link #convert2SystemLocation()} so that apps querying the GPS provider
 * receive the spoofed coordinates.
 * </p>
 *
 * @see BCell
 * @see BLocationConfig
 */
public class BLocation implements Parcelable {

    /** The latitude in degrees, ranging from -90.0 to 90.0. */
    private double mLatitude = 0.0;

    /** The longitude in degrees, ranging from -180.0 to 180.0. */
    private double mLongitude = 0.0;

    /** The altitude above the WGS84 reference ellipsoid in meters. */
    private double mAltitude = 0.0f;

    /** The ground speed in meters per second. */
    private float mSpeed = 0.0f;

    /** The bearing (direction of travel) in degrees, ranging from 0.0 to 360.0. */
    private float mBearing = 0.0f;

    /** The estimated horizontal accuracy radius in meters. */
    private float mAccuracy = 0.0f;
//    private float mHorizontalAccuracyMeters = 0.0f;
//    private float mVerticalAccuracyMeters = 0.0f;
//    private float mSpeedAccuracyMetersPerSecond = 0.0f;
//    private float mBearingAccuracyDegrees = 0.0f;

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mAltitude);
        dest.writeFloat(this.mSpeed);
        dest.writeFloat(this.mBearing);
        dest.writeFloat(this.mAccuracy);
    }

    /**
     * Returns the latitude of this location.
     *
     * @return the latitude in degrees
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Returns the longitude of this location.
     *
     * @return the longitude in degrees
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Default constructor for creating an empty {@link BLocation} at coordinates (0, 0).
     */
    public BLocation() {
    }

    /**
     * Constructs a {@link BLocation} with the specified latitude and longitude.
     *
     * @param latitude   the latitude in degrees
     * @param mLongitude the longitude in degrees
     */
    public BLocation(double latitude, double mLongitude) {
        this.mLatitude = latitude;
        this.mLongitude = mLongitude;
    }

    /**
     * Constructs a {@link BLocation} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    public BLocation(Parcel in) {
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mAltitude = in.readDouble();
        this.mAccuracy = in.readFloat();
        this.mSpeed = in.readFloat();
        this.mBearing = in.readFloat();
    }

    /**
     * Checks whether this location has default (zero) coordinates.
     *
     * @return {@code true} if both latitude and longitude are zero
     */
    public boolean isEmpty() {
        return mLatitude == 0 && mLongitude == 0;
    }

    public static final Parcelable.Creator<BLocation> CREATOR = new Parcelable.Creator<BLocation>() {
        @Override
        public BLocation createFromParcel(Parcel source) {
            return new BLocation(source);
        }

        @Override
        public BLocation[] newArray(int size) {
            return new BLocation[size];
        }
    };

    /**
     * Returns a string representation of this location for debugging.
     *
     * @return a debug string containing latitude, longitude, altitude, speed, bearing, and accuracy
     */
    @Override
    public String toString() {
        return "BLocation{" +
                "latitude: " + mLatitude +
                ", longitude: " + mLongitude +
                ", altitude: " + mAltitude +
                ", speed: " + mSpeed +
                ", bearing: " + mBearing +
                ", accuracy: " + mAccuracy +
                '}';
    }

    /**
     * Converts this virtual location to a standard Android {@link Location} object
     * using the GPS provider. The returned location includes simulated satellite
     * count extras to appear realistic to applications.
     *
     * @return a new {@link Location} populated with this instance's coordinates and metadata
     */
    public Location convert2SystemLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);
        location.setSpeed(mSpeed);
        location.setBearing(mBearing);
        location.setAccuracy(40f);
        location.setTime(System.currentTimeMillis());
        Bundle extraBundle = new Bundle();
        // GPS satellite number
        int satelliteCount = 10;
        extraBundle.putInt("satellites", satelliteCount);
        extraBundle.putInt("satellitesvalue", satelliteCount);
        location.setExtras(extraBundle);
        return location;
    }
}
