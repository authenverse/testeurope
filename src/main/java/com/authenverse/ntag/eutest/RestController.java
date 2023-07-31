package com.authenverse.ntag.eutest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestHeader;


import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import static java.rmi.server.LogStream.log;


@org.springframework.web.bind.annotation.RestController
public class RestController {

    private String api_key = "bfwOcPIGHtZh2QwZn7neUlyc7gV9Cm3wq6StIBZdtD0=";
    private String data_key = "authenverse12345";

    @PostMapping("/v1/auth")
    public String auth(@RequestBody String body
    ,@RequestHeader Map<String, String> headers

    ) {
        String apikey = headers.get("authorization");
        headers.forEach((key, value) -> {
            System.out.println("key:value = " + key+":"+value);
        });

        System.out.println("apikey = " + apikey);
        System.out.println("original = " + api_key);
        Response r = new Response();


       if (!api_key.equals(apikey)) {
            r.resultCode = 9000;
            r.resultMessage="WRONG API KEY";
            return  new Gson().toJson(r);
        }

        r.tag = new Tag();
        r.tag.data = "";

        JsonObject jo = null;
        try {

            jo = new Gson().fromJson(body, JsonObject.class);
            String msg = jo.get("e").getAsString();
            System.out.println("input is  = " + msg );
            byte[] key = new byte[16];
            byte[] iv = new byte[16];


            byte[] dec = AES.decrypt(iv,key,s(msg));


            String id = j(dec, 1, 8, false);
            String counter = j(dec, 8, 11, true);

            TagData tagData = new TagData();
            tagData.id = id;
            tagData.cnt = Integer.parseInt(counter, 16);

            String tagJson = new Gson().toJson(tagData);

            byte[] encodeKey = data_key.getBytes();
            System.out.printf("key is  " + ByteArrayHexStringConverter.byteArrayToHexString(encodeKey));
            byte[] encoded = AES.encrypt(iv,data_key.getBytes(),
                    tagJson);



            System.out.println("id = " + id);
            System.out.println("counter = " + counter);
            r.tag.data = Base64.encodeBase64String(encoded);

            String isend = "NQhXMcckKKwJQ0lNlFMbDri5EgU4cwEPSTCLFfmbouljIvSxMlRImZxm2PysT9zr";
            byte[] toDecode = Base64.decodeBase64(isend);
            byte[] decoded = AES.decrypt(iv,data_key.getBytes(),toDecode);
            System.out.println("decoded = " + new String(decoded));


        } catch (Exception e) {
            System.out.println("error  " + e.getMessage());
        }
        return new Gson().toJson(r);
    }


    public String j(byte[] data, int from, int to, boolean _reverse) {
        if (data.length == 0) return "";
        byte[] newData = Arrays.copyOfRange(data, from, to);
        if (_reverse) {
            reverse(newData);
        }

        return ByteArrayHexStringConverter.toHexString(newData);
    }

    public byte[] s(String hex) {
        return ByteArrayHexStringConverter.hexStringToByteArray(hex.toUpperCase());
    }


    public void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

}