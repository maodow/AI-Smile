package com.example.ai_smile.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Admin on 2020/5/22.
 *
 */

public class StringUtil {

    private static final String TAG = "StringUtil";

    public static Intent[] parseIntents(String uris){
        if(TextUtils.isEmpty(uris)) {
            Log.e(TAG, "parseIntents uris is null");
            return null;
        }
        String[] intentStringArray = uris.split("end", 0);
        if(intentStringArray.length == 0){
            return null;
        }

        Intent[] intents = new Intent[intentStringArray.length];

        for (int i = 0; i < intentStringArray.length; i++) {
            String intentStr = intentStringArray[i] + "end";
            intents[i] = parseIntent(intentStr);
        }
        return intents;
    }

    /**
     * 根据Uri String 解析成Intent
     * @param uri android-app://component=com.my.package%2f.package.MyActivity;i.int_params=1;launchFlags=0x07000000;end
     * @return Intent
     */
    public static Intent parseIntent(String uri){
        if(TextUtils.isEmpty(uri)) {
            Log.e(TAG, "parseIntent uri is null");
            return null;
        }
        Intent intent = new Intent();
        Bundle bundle = null;
        int i = 0;
        if(uri.startsWith("android-app://")){
            i = 14;
            while (i >= 0 && !uri.startsWith("end", i)) {
                int eq = uri.indexOf('=', i);
                if (eq < 0) eq = i-1;
                int semi = uri.indexOf(';', i);
                String value = eq < semi ? Uri.decode(uri.substring(eq + 1, semi)) : "";

                // action
                if (uri.startsWith("action=", i)) {
                    intent.setAction(value);
                }

                // categories
                else if (uri.startsWith("category=", i)) {
                    intent.addCategory(value);
                }

                //data
                else if(uri.startsWith("data=", i)){
                    intent.setData(Uri.parse(value));
                }

                // launch flags
                else if (uri.startsWith("launchFlags=", i)) {
                    int flags = Integer.decode(value);
                    intent.setFlags(flags);
                }

                // package
                else if (uri.startsWith("package=", i)) {
                    intent.setPackage(value);
                }

                // component
                else if (uri.startsWith("component=", i)) {
                    intent.setComponent(ComponentName.unflattenFromString(value));
                }
                // extra
                else {
                    String key = Uri.decode(uri.substring(i + 2, eq));
                    // create Bundle if it doesn't already exist
                    if (bundle == null) bundle = new Bundle();
                    // add EXTRA
                    if      (uri.startsWith("S.", i)) bundle.putString(key, value);
                    else if (uri.startsWith("B.", i)) bundle.putBoolean(key, Boolean.parseBoolean(value));
                    else if (uri.startsWith("b.", i)) bundle.putByte(key, Byte.parseByte(value));
                    else if (uri.startsWith("c.", i)) bundle.putChar(key, value.charAt(0));
                    else if (uri.startsWith("d.", i)) bundle.putDouble(key, Double.parseDouble(value));
                    else if (uri.startsWith("f.", i)) bundle.putFloat(key, Float.parseFloat(value));
                    else if (uri.startsWith("i.", i)) bundle.putInt(key, Integer.parseInt(value));
                    else if (uri.startsWith("l.", i)) bundle.putLong(key, Long.parseLong(value));
                    else if (uri.startsWith("s.", i)) bundle.putShort(key, Short.parseShort(value));
                    else return null;


                }

                // move to the next item
                i = semi + 1;
            }

            if(bundle != null)
                intent.putExtras(bundle);
        }else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        return intent;
    }

    /**
     * 根据时间字符串及时间格式返回时间毫秒数
     * @param dateString 时间字符串
     * @param timeType 时间格式
     */
    public static long string2Millis(String dateString, String timeType, boolean isTimeZone){
        long result = 0;
        DateFormat dateFormat = new SimpleDateFormat(timeType, Locale.US);
        if(isTimeZone)
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = dateFormat.parse(dateString);
            result = date.getTime();
        } catch (Exception e) {
            Log.e(TAG, String.format("解析时间失败 dateString:%s, timeType:%s",dateString, timeType)) ;
        }
        return result;
    }

    public static String millis2String(long millis, String timeType, boolean isTimeZone){
        String result = "";
        DateFormat dateFormat = new SimpleDateFormat(timeType, Locale.US);
        if(isTimeZone)
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            result = dateFormat.format(new Date(millis));
        } catch (Exception e) {
            Log.e(TAG, String.format("解析时间失败 millis:%s, timeType:%s", millis, timeType));
        }
        return result;
    }

    /**
     * 将时间戳翻译成文字(1400000000 -> 刚刚)
     * @param millis
     * @return
     */
    public static String translateMillis(long millis){
        long second = millis / 1000;
        long difference = System.currentTimeMillis() / 1000 - second;

        if(difference < 60){
            return "刚刚";
        }
        if(difference < 3600){
            return (int)(difference / 60) + "分钟前";
        }
        if(difference < 86400){
            return (int)(difference / 3600) + "小时前";
        }
        if(difference < 86400 * 365){
            return millis2String(millis, "MM-dd", false);
        }
        return millis2String(millis, "yyyy-MM-dd", false);
    }

    public static String hiddenPhoneNumber(String phone){
        if(phone == null || phone.length() < 4)
            return phone;
        char[] chars = phone.toCharArray();
        int length = chars.length;
        int start = Math.max(0, (length / 2) - 2);
        int end = Math.min(start + 4, length);
        for(int i = start; i < end; i++){
            chars[i] = '*';
        }
        return String.valueOf(chars);
    }
    public static String clearSpaceStr(String string){
        String s = string.replaceAll(" ","");
        return s;
    }
}
