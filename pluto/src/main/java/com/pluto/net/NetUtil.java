package com.pluto.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class NetUtil {
    //
    private static final String TAG = "PlutoNetUtil";

    /**
     *
     * @param urlPath 请求地址
     * @param bearer 认证参数
     * @param params 参数
     * @param listener 网络请求回调监听
     */
    public static void get(String urlPath, String bearer, Map<String, Object> params, NetListener listener) {
        new Thread(() -> {
            try {
                String newPath = urlPath;
                String content = map2UrlStr(params);
                if (!content.equals("")) {
                    newPath += content;
                }
                URL url = new URL(newPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置请求方法
                connection.setRequestMethod("GET");
                //设置连接超时时间（毫秒）
                connection.setConnectTimeout(5000);
                //设置读取超时时间（毫秒）
                connection.setReadTimeout(5000);
                //设置编码格式json
                connection.setRequestProperty("Content-Type", "application/json");
                if (bearer != null && !bearer.equals("")) {
                    connection.addRequestProperty("authorization", "Bearer " + bearer);
                }
                //
                int responseCode = connection.getResponseCode();
                Log.i(TAG, "net get response code:" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //返回输入流
                    InputStream in = connection.getInputStream();
                    //读取输入流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    //关闭流
                    reader.close();
                    //关闭连接
                    connection.disconnect();
                    //
                    if (listener != null) {
                        listener.onNetResponse(true, result.toString(), null);
                    }
                } else {
                    if (listener != null) {
                        listener.onNetResponse(false, null, "Net Error");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onNetResponse(false, null, "Net Error");
                }
            }
        }).start();
    }

    /**
     * HTTP POST请求
     * @param urlPath 请求地址
     * @param bearer 认证参数
     * @param params 如果没有参数传null
     * @param listener 网络请求回调监听
     */
    public static void post(String urlPath, String bearer, Map<String, Object> params, NetListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置请求方法
                connection.setRequestMethod("POST");
                //设置连接超时时间（毫秒）
                connection.setConnectTimeout(5000);
                //设置读取超时时间（毫秒）
                connection.setReadTimeout(5000);
                //设置是否向 HttpUrlConnection 输出，对于post请求，参数要放在 http 正文内，因此需要设为true，默认为false。
                connection.setDoOutput(true);
                //设置是否使用缓存
                connection.setUseCaches(false);
                //设置编码格式json
                connection.setRequestProperty("Content-Type", "application/json");
                if (bearer != null && !bearer.equals("")) {
                    connection.addRequestProperty("authorization", "Bearer " + bearer);
                }
                //设置参数
                String content = map2JsonStr(params);
                if (!content.equals("")) {
                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                    os.writeBytes(content);
                    os.flush();
                    os.close();
                }
                //
                int responseCode = connection.getResponseCode();
                Log.i(TAG, "net post response code:" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //返回输入流
                    InputStream in = connection.getInputStream();
                    //读取输入流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    //关闭流
                    reader.close();
                    //关闭连接
                    connection.disconnect();
                    //
                    if (listener != null) {
                        listener.onNetResponse(true, result.toString(), null);
                    }
                } else {
                    if (listener != null) {
                        listener.onNetResponse(false, null, "Net Error");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onNetResponse(false, null, "Net Error");
                }
            }
        }).start();
    }

    /**
     * 将map参数转换成json字符串
     * @param map 需要转换的map
     * @return 当map不为空时返回json字符串，如果参数为空返回空字符串
     */
    private static String map2JsonStr(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        Set<String> keys = map.keySet();
        if (keys.isEmpty()) {
            return "";
        }
        String temp = "";
        try {
            JSONObject obj = new JSONObject();
            for (String key : keys) {
                if (map.get(key) == null) {
                    continue;
                }
                obj.put(key, map.get(key));
            }
            temp = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     *
     * 将map参数转换成http get请求参数的字符串
     * @param map 需要转换的map
     * @return 当map不为空时返回字符串，如果参数为空返回空字符串。返回数据格式为 ?a="xxx"&b="xxx"
     */
    private static String map2UrlStr(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        Set<String> keys = map.keySet();
        StringBuilder temp = new StringBuilder();
        int i = 0;
        for (String key : keys) {
            if (map.get(key) == null) {
                continue;
            }
            if (i == 0) {
                temp.append("?");
            } else {
                temp.append("&");
            }
            temp.append(key).append("=").append(map.get(key));
            i++;
        }
        return temp.toString();
    }
}
