package com.authenverse.ntag.eutest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EutestApplication {

    public static void main(String[] args) {
        String[] argument = new String[args.length + 2];

        System.arraycopy(args, 0, argument, 0, args.length);

        argument[args.length] = "--async.log.level=INFO";
        argument[args.length + 1] = "--async.log.output=console";


        SpringApplication.run(EutestApplication.class, argument);
    }

}
