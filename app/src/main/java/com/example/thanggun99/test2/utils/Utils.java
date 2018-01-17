package com.example.thanggun99.test2.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thanggun99.test2.App;
import com.example.thanggun99.test2.BuildConfig;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@SuppressWarnings("ALL")
public class Utils {
    private static int id = 0;

    @SuppressLint("SimpleDateFormat")
    public static Date convertSecondToLocalDate(long timeInSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInSeconds * 1000);

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateString = format.format(calendar.getTime());

//        return formatStringToDate(dateString, "yyyy-MM-dd HH:mm:ss");
        return new Date(dateString);
    }

    public static Date dateFromUTC(Date date) {
        return new Date(date.getTime() + Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public static Date dateToUTC(Date date) {
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public static String convertIntegerToMoneyString(int money) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###", symbols);
        return decimalFormat.format(money);
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateToString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateToString(String dateString, String format) {
        Date date = formatStringToDate(dateString, format);
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date formatStringToDate(String dateString, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String doubleEscapeTeX(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\"') builder.append("\\");
            builder.append(s.charAt(i));
        }
        return builder.toString();
    }

    public static boolean isEnableGPS() {
        LocationManager locationManager = (LocationManager) App.getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static String encryptMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(number.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void showSoftKeyboard(View view) {
        if (view != null && view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    public static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmap(SurfaceView surfaceView) {
        surfaceView.setDrawingCacheEnabled(true);
        surfaceView.buildDrawingCache(true);
        final Bitmap bitmap = Bitmap.createBitmap(surfaceView.getDrawingCache(true));
        surfaceView.destroyDrawingCache();
        surfaceView.setDrawingCacheEnabled(false);

        return bitmap;
    }

    //    wlan0, eth0
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    @SuppressLint("MissingFirebaseInstanceTokenRefresh")
    public static String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public static void builderParams(Uri.Builder builder, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    public static String convertToParams(Map<String, Object> params) {
        Uri.Builder builder = new Uri.Builder();
        builderParams(builder, params);
        return builder.build().getQuery();
    }

    public static void showNotify(String title, String message, int residSmallIcon) {
/*        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext(), "")
                .setSmallIcon(residSmallIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(id, builder.build());
            id++;
        }
    }

    public static void showToast(String message) {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = null;
        if (connectivityManager != null) {
            info = connectivityManager.getActiveNetworkInfo();
        }
        return info != null && info.isConnected();
    }

    //run on thread
    public static boolean isConnectAvalilabe() {
        String host = "ehrs.com.vn";
        int port = 80;
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(host, port), 2000);
            socket.close();
            return true;
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public static void goToSettingsAutoStartForXixaomi(Context context) {
        if ("xixaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            context.startActivity(intent);
        }
    }

    public static String convertColorToHex(Drawable drawable) {

        return String.format("#%06x", (((ColorDrawable) drawable).getColor() & 0x00FFFFFF));
    }

    public static void showLog(String message) {
        if (!TextUtils.isEmpty(message)) {
            showLog("Thanggggggggggggg", message);
        }
    }

    public static void showLog(String tag, String message) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message) && BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void refreshResource(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                refreshResource((ViewGroup) view);
            else {
                if (view.getTag() == null) {
                    continue;
                }
                int resid = App.getContext().getResources()
                        .getIdentifier(view.getTag().toString(), "string", App.getContext().getPackageName());
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.setHint(resid);
                } else if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setText(resid);
                }
            }
        }
    }

    public static void addItemDecorationForNav(Context context, NavigationView navigationView) {
        for (int i = 0; i < navigationView.getChildCount(); i++) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(i);

            if (navigationMenuView != null) {
                navigationMenuView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            }
        }
    }

    public static float convertDpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, App.getContext().getResources().getDisplayMetrics());
    }

    public static void setLocale(Locale locale) {
        Resources resources = App.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.locale = locale;
        } else {
            configuration.setLocale(locale);
        }
        resources.updateConfiguration(configuration, metrics);
    }

    public static int getColorRes(@ColorRes int colorRes) {
        return ContextCompat.getColor(App.getContext(), colorRes);
    }

}
