package hu.kts.cmetronome;

import com.google.android.gms.ads.AdRequest;

/**
 * To get the device id, filter logcat for the tag "ads" and there will be a similar row:
 * Use AdRequest.Builder.addTestDevice("XXXXXXXXXXXXXXXXXXXXXXXXX") to get test ads on this device.
 */
public final class AdMobTestDeviceFilteredBuilderFactory {

    private AdMobTestDeviceFilteredBuilderFactory() {};


    public static AdRequest.Builder get() {
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("21A7F45D4BDEB3BCCD96A1057F1DB48A") //Oneplus One
                .addTestDevice("48A2EDAB0C14F4914EFC0654AFC27369") //Nexus 5
                ;
    }
}
