package io.dreamz.simple2fa;

import io.dreamz.simple2fa.utils.Base32String;
import io.dreamz.simple2fa.utils.HOTP;

import java.io.IOException;
import java.text.ParseException;

public class Main {


    public static void main(String[] args) throws IOException, ParseException, Base32String.DecodingException {
        final HOTP hotp = new HOTP();

        final String myKey = "5FEH6PDRT2T4HW2EGMQVUWJRRIXWZL7R";

        String code = hotp.generate("SHA1", Base32String.decode(myKey));

        System.out.println("your 2fa code is " + code);
    }
}
