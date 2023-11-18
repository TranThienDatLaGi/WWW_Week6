package vn.edu.iuh.fit.frontend.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class SecUtils {

    public static String hash(String password){
        byte[] s = BCrypt.withDefaults().hash(17, password.toCharArray());
        return new String(s);
    }

//    public static void main(String[] args) {
//        SecUtils sec=new SecUtils();
//        String h = sec.hash("teoteo");
//        System.out.println(h);
//    }
}
