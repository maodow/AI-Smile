package com.example.ai_smile.utils;

/**
 * Created by Administrator on 2019/4/22.
 *
 */
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.annotation.Nullable;

import com.example.ai_smile.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 管理程序里Sp存储
 */
public class SpUtils {

    public static final String TAG = SpUtils.class.getSimpleName();
    public static final String USERINFO = "userInfo";
    public static final String LOGIN_URL = "LOGIN_URL";
    public static final String ISFirst = "IsFirst";
    public static final String ISLOGIN = "hasLogin";

    private SharedPreferences mSp;
    private static ReentrantLock mLock = new ReentrantLock();
    private static SpUtils mInstance;


    public static SpUtils getInstance(){
        try{
            mLock.lock();
            if(null == mInstance){
                mInstance = new SpUtils(null);
            }
            return mInstance;
        }finally{
            mLock.unlock();
        }
    }

    private static final String SP_NAME = "config";
    private static final int SP_OPEN_MODE = 0;

    private SpUtils(SharedPreferences sharedPreferences) {
        if(sharedPreferences == null) {
            mSp = App.getApplicationInstance().getSharedPreferences(SP_NAME, SP_OPEN_MODE);
        }else{
            mSp = sharedPreferences;
        }

    }

    public static SpUtils mock() {
        return mInstance = new SpUtils(new SharedPreferences() {
            @Override
            public Map<String, ?> getAll() {
                return null;
            }

            @Nullable
            @Override
            public String getString(String key, String defValue) {
                return null;
            }

            @Nullable
            @Override
            public Set<String> getStringSet(String key, Set<String> defValues) {
                return null;
            }

            @Override
            public int getInt(String key, int defValue) {
                return 0;
            }

            @Override
            public long getLong(String key, long defValue) {
                return 0;
            }

            @Override
            public float getFloat(String key, float defValue) {
                return 0;
            }

            @Override
            public boolean getBoolean(String key, boolean defValue) {
                return false;
            }

            @Override
            public boolean contains(String key) {
                return false;
            }

            @Override
            public Editor edit() {
                return new Editor() {
                    @Override
                    public Editor putString(String key, String value) {
                        return this;
                    }

                    @Override
                    public Editor putStringSet(String key, Set<String> values) {
                        return this;
                    }

                    @Override
                    public Editor putInt(String key, int value) {
                        return this;
                    }

                    @Override
                    public Editor putLong(String key, long value) {
                        return this;
                    }

                    @Override
                    public Editor putFloat(String key, float value) {
                        return this;
                    }

                    @Override
                    public Editor putBoolean(String key, boolean value) {
                        return this;
                    }

                    @Override
                    public Editor remove(String key) {
                        return this;
                    }

                    @Override
                    public Editor clear() {
                        return this;
                    }

                    @Override
                    public boolean commit() {
                        return true;
                    }

                    @Override
                    public void apply() {

                    }
                };
            }

            @Override
            public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

            }

            @Override
            public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

            }
        });
    }

    /**
     * 保存字符串到sp文件
     * @param key 字符串对应的key
     * @param value 字符串的vaule
     * @return true 保存成功  false 保存失败
     */
    public boolean saveStringToSp(String key , String value){

        if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
           // JLog.e(TAG, "=== saveStringToSp(),保存失败,key不能为null, 自动删除 === ");
            remove(key);
            return false;
        }
        mSp.edit().putString(key, value).apply();
//        JLog.e(TAG, key+"=== saveStringToSp(),保存成功 "+value);
        return true;
    }

    /**
     * 删除字段
     */
    public boolean remove(String key){

        if(TextUtils.isEmpty(key)){
            return false;
        }
        mSp.edit().remove(key).apply();
        return true;
    }

    /**
     *
     * 保存整形值到sp
     * @param key 整形值对应的key
     * @param value 整形值对应的value
     * @return true 保存成功  false 保存失败
     */
    public boolean saveIntToSp(String key , int value){

        if(TextUtils.isEmpty(key)){
            return false;
        }
        mSp.edit().putInt(key, value).apply();
        return true;
    }

    /**
     *
     * 保存整形值到sp
     * @param key 整形值对应的key
     * @param value 整形值对应的value
     * @return true 保存成功  false 保存失败
     */
    public boolean saveLongToSp(String key , long value){

        if(TextUtils.isEmpty(key)){
            return false;
        }
        mSp.edit().putLong(key, value).apply();
        return true;
    }

    /**
     *
     * 保存布尔值到sp
     * @param key 布尔值对应的key
     * @param value 布尔值对应的value
     * @return true 保存成功 fasle 保存失败
     */
    public boolean saveBoolenTosp(String key , boolean value){

        if(TextUtils.isEmpty(key)){
            return false;
        }
        mSp.edit().putBoolean(key, value).apply();
        return true;
    }

    /**
     * 根据保存到sp的key 取出String value
     * @param key  String对应的key
     * @param defaultValue String对应的Value
     */
    public String getStringValue(String key , String defaultValue){

        if(TextUtils.isEmpty(key)){
            return defaultValue;
        }
        return mSp.getString(key, defaultValue);
    }

    public JSONArray getJSONArray(String key){
        if (TextUtils.isEmpty(key)){
            return new JSONArray();
        }

        String jsonStr = mSp.getString(key, "[]");
        JSONArray json;
        try {
            json = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
        return json;
    }

    public JSONObject getJSONObject(String key){
        if (TextUtils.isEmpty(key)){
            return new JSONObject();
        }

        String jsonStr = mSp.getString(key, "{}");
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
        return json;
    }

    /**
     * 根据保存到sp的key 取出Int value
     * @param key 保存的key
     * @param defaultValue 默认值
     * @return key 对应的 int value
     */
    public int getIntValue(String key , int defaultValue){

        if(TextUtils.isEmpty(key)){
            return defaultValue;
        }
        return mSp.getInt(key, defaultValue);

    }

    /**
     * 根据保存到sp的key 取出Int value
     * @param key 保存的key
     * @param defaultValue 默认值
     * @return key 对应的 int value
     */
    public long getLongValue(String key , long defaultValue){

        if(TextUtils.isEmpty(key)){
            return defaultValue;
        }
        return mSp.getLong(key, defaultValue);

    }

    /**
     * 根据保存到sp的key Boolean value
     * @param key 保存的key
     * @param defaultValue 默认值
     * @return key 对应的 Boolean value
     */
    public boolean getBooleanValue(String key , boolean defaultValue){
        if(TextUtils.isEmpty(key)){
            return defaultValue;
        }
        return mSp.getBoolean(key, defaultValue);
    }

}
