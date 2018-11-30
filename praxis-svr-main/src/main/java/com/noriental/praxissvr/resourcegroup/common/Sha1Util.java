package com.noriental.praxissvr.resourcegroup.common;

import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.validate.exception.BizLayerException;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kate on 2016/11/21.
 */
public class Sha1Util {

    /***
     *  SHA1 安全加密算法
     * @param timestamp
     * @return
     * @throws DigestException
     */
    public static String SHA1(String timestamp) throws BizLayerException {
        if (timestamp==null||timestamp.length()==0){
            return null;
        }
        try {
            //指定sha1算法
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update( timestamp.getBytes("UTF-8"));
            //获取字节数组
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new BizLayerException("", PraxisErrorCode.SHA1_EQUAL_EXCEPTION);
        }
    }

  /*  public static void main(String[] args)throws Exception{

        System.out.print(SHA1("1479870742062"));
    }*/


}
