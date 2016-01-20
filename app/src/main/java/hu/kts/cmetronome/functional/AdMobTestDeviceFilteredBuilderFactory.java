package hu.kts.cmetronome.functional;

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
                .addTestDevice("DEF436D888219AF0E490690759A15C54") //Acro S
                .addTestDevice("21A7F45D4BDEB3BCCD96A1057F1DB48A") //Oneplus One
                .addTestDevice("66D50DBE3EB178B2906A0159E37438B6") //Xperia T
                ;
    }
}
