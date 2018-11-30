package com.noriental.praxissvr.answer.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * 通用方法
 *
 * @author liuhuapeng
 * @date 2016年5月28日16:57:23
 */
public final class CommonUtil {

    /**
     * 公共上行必选参数验证
     */
    public static void checkUpstreamCommonParam(String rid,Map<String, Object> paramMap) throws AppException {
        if (!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_UA))) {
            throw new AppException("[" + rid + "] " + AppConstants.STRING_UA + " must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if (!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_OS))) {
            throw new AppException("["+rid+"] "+AppConstants.STRING_OS+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if (!org.springframework.util.StringUtils.hasText( paramMap.get(AppConstants.STRING_VC).toString())) {
            throw new AppException("["+rid+"] "+AppConstants.STRING_VC+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if (!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_SW))) {
            throw new AppException("["+rid+"] "+AppConstants.STRING_SW+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if (!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_SH))) {
            throw new AppException("["+rid+"] "+AppConstants.STRING_SH+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if (!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_CHANNLE))) {
            throw new AppException("["+rid+"] "+AppConstants.STRING_CHANNLE+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if (!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_UDID))) {
            throw new AppException("["+rid+"] "+AppConstants.STRING_UDID+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if (!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_VS))) {
            throw new AppException("["+rid+"] "+AppConstants.STRING_VS+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
        if(!org.springframework.util.StringUtils.hasText((String) paramMap.get(AppConstants.STRING_SERIAL))){
            throw new AppException("["+rid+"] "+AppConstants.STRING_SERIAL+" must not be blank.", AppReturnCode.APP_ERROR_PARAM);
        }
    }

    /**
     * 从html中提取所有的图片src
     *
     * @param htmlStr
     * @return
     */
    /*public static List<String> getImgStr(String htmlStr) {
        List<String> pics = new ArrayList<String>();
        String regexImage = "<img.+?src\\s*=\\s*['|\"]?\\s*([^'\"\\s>]+).+?/?>?";

        Pattern p = Pattern.compile(regexImage, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlStr);

        if (m.find()) {
            //ImageStr = m.group();
            String  ImageSrcStr = m.group(1);
            //System.out.println(ImageStr);
            //System.out.println(ImageSrcStr);
            pics.add(ImageSrcStr);
        }
        return pics;
    }
*/

    public static  List<String> getImgSrc(String htmlStr) {
        String img = "";
        Pattern p_image;
        Matcher m_image;
        List<String> pics = new ArrayList<String>();
        //       String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            img = img + "," + m_image.group();
            // Matcher m =
            // Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }
    /**
     * 获取请求参数json
     *
     * @param request
     * @return
     * @author qianchun  @date 2015年8月17日 下午2:00:59
     */
    /*public static String getRequestData(HttpServletRequest request) {
        return getBodyString(request);
    }*/
    /**
     * 读取request中的数据
     *
     * @param request
     * @return
     */
    /*private static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }*/

    /*public static String getRequestGzipStr(HttpServletRequest request) {
        try {

            InputStream inputStream = request.getInputStream();
            return decompress(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /**
     * 解析压缩流
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String decompress(InputStream inputStream) throws IOException {

        GZIPInputStream gis = new GZIPInputStream(inputStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        int count;
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        while ((count = gis.read(buffer, 0, bufferSize)) != -1) {
            baos.write(buffer, 0, count);
        }

        gis.close();
        String temp = new String(baos.toByteArray(), "UTF-8");
        baos.close();
        return temp;
    }

    /*public static boolean isMultipartContent(HttpServletRequest request) {
        if(!AppConstants.STRING_POST.equalsIgnoreCase(request.getMethod())) {
            return false;
        } else {
            String contentType = request.getContentType();
            return contentType!=null && contentType.toLowerCase().startsWith("multipart/");
        }
    }*/


    /**
     * 获取IP
     * @param request
     * @return
     */
    /*public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }*/
    /**
     * map 转化为 get请求所需的拼接字符串
     * @param params
     * @return
     */
    public static String paramToHttpGetParam( Map<String,String> params){
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new TreeMap<String,String>(params);
        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
        // 遍历排序后的字典，将所有参数按"key=value&"格式拼接在一起
        StringBuilder basestring = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            basestring.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        String resultStr=basestring.toString();
        // 去除字符末尾的“&”
        resultStr=resultStr.substring(0,resultStr.length()-1);
        return resultStr;
    }

    /**
     * map 转化为 get请求所需的拼接字符串
     * @param params
     * @return
     */
    public static String paramToHttpGetParams( Map<String,Object> params){
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, Object> sortedParams = new TreeMap<String,Object>(params);
        Set<Map.Entry<String, Object>> entrys = sortedParams.entrySet();
        // 遍历排序后的字典，将所有参数按"key=value&"格式拼接在一起
        StringBuilder basestring = new StringBuilder();
        for (Map.Entry<String, Object> param : entrys) {
            basestring.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        String resultStr=basestring.toString();
        // 去除字符末尾的“&”
        resultStr=resultStr.substring(0,resultStr.length()-1);
        return resultStr;
    }

    /**
     * 将字符串中含有unicode 转成中文
     * @param asciicode
     * @return
     */
    public static String ascii2native ( String asciicode ){
        String[] asciis = asciicode.split ("\\\\u");
        String nativeValue = asciis[0];
        try
        {
            for ( int i = 1; i < asciis.length; i++ )
            {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt (code.substring (0, 4), 16);
                if (code.length () > 4)
                {
                    nativeValue += code.substring (4, code.length ());
                }
            }
        }catch (NumberFormatException e) {
            e.printStackTrace();
            return asciicode;
        }
        return nativeValue;
    }
}
