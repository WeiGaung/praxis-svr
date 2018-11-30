package com.noriental.praxissvr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Discribe:
 * Project:studentpad_middle
 * Package: com.noriental.utils
 * User: Chengwenbo
 * Date:  2016/5/16
 * Time: .17:42
 */
public class HttpUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final int DEFAULT_CONNECT_TIMEOUT = 5 * 1000;

    private static final int DEFAULT_READ_TIMEOUT = 5 * 1000;

    private static final String METHOD_POST = "POST";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_TEXT = "application/x-www-form-urlencoded";
    
    /**
     * 获取IP
     * @param
     * @return
     */

    
    public static byte[] doGet(String str_url, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        InputStream input = null;
        try {

            URL url = new URL(str_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            if (connectTimeout <= 0) {
                conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            } else {
                conn.setConnectTimeout(connectTimeout);
            }
            if (readTimeout <= 0) {
                conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            } else {
                conn.setReadTimeout(readTimeout);
            }
            input = conn.getInputStream();
            return parseFromInputStream(input);
        } catch (Exception e) {
            LOGGER.error("deGet " + str_url + " error:{}", e);
            throw new IOException(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOGGER.error("deGet " + str_url + " error:{}", e);
                }
            }
        }
    }

    public static byte[] doGet(String str_url) throws IOException {
        //默认的超时时间connect 5s, read 5s
        return doGet(str_url, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    public static byte[] parseFromInputStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        int count;
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        while ((count = input.read(buffer, 0, bufferSize)) != -1) {
            baos.write(buffer, 0, count);
        }
        input.close();
        baos.close();
        return baos.toByteArray();
    }


    public static String doPostStr(String str_url, Map<String, String> params
            , int conn_timeout, int read_timeout, String encoding) {
        byte[] res_bytes = doPost(str_url, params, conn_timeout, read_timeout);
        try {
            return new String(res_bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(res_bytes);
        }
    }

    public static byte[] doPost(String str_url, Map<String, String> paramsMap, int conn_timeout, int read_timeout) {
        StringBuilder sb = new StringBuilder();
        if (paramsMap != null && paramsMap.size() > 0) {
            int i = 0;
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                if (i > 0) {
                    sb.append("&");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                i++;
            }
        }
        return doPost(str_url, sb.toString(), conn_timeout, read_timeout);
    }

    public static byte[] doPost(String str_url, String params, int conn_timeout, int read_timeout) {
        return doPost(str_url, params, conn_timeout, read_timeout, new HashMap<String, String>());
    }

    public static byte[] doPost(String str_url, String params, int conn_timeout, int read_timeout, Map<String, String> headerMap) {
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL url = new URL(str_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            // Read from the connection. Default is true.
            connection.setDoInput(true);
            // Set the post method. Default is GET
            connection.setRequestMethod(METHOD_POST);
            // Post cannot use caches
            // Post 请求不能使用缓存
            connection.setUseCaches(false);

            connection.setConnectTimeout(conn_timeout);

            connection.setReadTimeout(read_timeout);
            // This method takes effects to
            // every instances of this class.
            // URLConnection.setFollowRedirects是static函数，作用于所有的URLConnection对象。
            // connection.setFollowRedirects(true);

            // This methods only
            // takes effacts to this
            // instance.
            // URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
            connection.setInstanceFollowRedirects(false);

            // Set the content type to urlencoded,
            // because we will write
            // some URL-encoded content to the
            // connection. Settings above must be set before connect!
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
            // 进行编码
            connection.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_TEXT);
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            connection.setRequestProperty("connection", "close");

            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.connect();


            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

            out.write(params.getBytes("UTF-8"));
//            out.writeBytes(params);
            out.flush();
            out.close();
            input = connection.getInputStream();
            return parseFromInputStream(input);
        } catch (Exception e) {
            LOGGER.error("HttpUtils Post Error", e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static String doGetStr(String urlStr)
    {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try
        {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (conn.getResponseCode() == 200)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1)
                {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else
            {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
            }
            conn.disconnect();
        }

        return null ;

    }
}
