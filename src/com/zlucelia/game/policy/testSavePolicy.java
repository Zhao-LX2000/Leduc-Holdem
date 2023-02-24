package com.zlucelia.game.policy;

import java.io.*;
import java.util.Date;

public class testSavePolicy {
    public static void main(String[] args) throws IOException {
        CFRPolicy cfrPolicy = new CFRPolicy("");
        File f=new File("policy_"  + new Date(System.currentTimeMillis()).getTime());
        FileOutputStream out=new FileOutputStream(f);
        ObjectOutputStream objwrite=new ObjectOutputStream(out);
        objwrite.writeObject(cfrPolicy);
        objwrite.flush();
        objwrite.close();
    }
}
