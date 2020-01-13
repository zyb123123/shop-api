package com.fh.utils;

import java.security.MessageDigest;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;

/**
 * <pre>项目名称：movie-admin
 * 类名称：MD5Util
 * 类描述：MD5加密工具类(包含加盐算法)
 * 创建人：李川 lichuan@163.com
 * 创建时间：2019年10月30日 上午9:56:04
 * 修改人：李川 lichuan@163.com
 * 修改时间：2019年10月30日 上午9:56:04
 * 修改备注：
 * @version </pre>
 */
public class MD5Util {

    /**
     * 生成含有随机盐的密码
     */
    public static String generate(String password) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        String salt = sb.toString();
        password = md5Hex(password + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 校验密码是否正确
     */
    public static boolean verify(String password, String md5) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        return md5Hex(password + salt).equals(new String(cs1));
    }

    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    public static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] a) {
        String str = MD5Util.generate("123456");
        System.out.println(str);
        String str2 = MD5Util.generate("123456");
        System.out.println(str2);
        System.out.println(MD5Util.verify("123456", "a8a669b1290fc52f0195f25451e12f35b54c93bd28028040"));
        System.out.println(MD5Util.verify("123456", "e8f87e379e55f3460da8f944840b2ce7dc5159df3ce46a2e"));
        System.out.println(MD5Util.generate(MD5Util.md5Hex("123456")));
    }

}
