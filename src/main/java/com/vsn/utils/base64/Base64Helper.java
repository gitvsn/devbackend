package com.vsn.utils.base64;
import org.apache.commons.codec.binary.Base64;


public class Base64Helper {

    public static String toBase64(String str){
        return new String(Base64.encodeBase64(str.getBytes()));
    }

    public static String fromBase64(String str){
        return  new String(Base64.decodeBase64(str));
    }

    public static boolean isBase64(String str){
        return  !fromBase64(str).contains("�");
    }
}
