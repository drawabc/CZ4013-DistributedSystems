package test;

import java.util.Scanner;

import client.Utils;

public class TestMarshal {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Data Type: (1) Int\t(2) String");
        int dataType = Integer.parseInt(sc.nextLine());

        if (dataType == 1) {
            System.out.println("Enter an integer");
            int x = Integer.parseInt(sc.nextLine());
            byte[] marshalledInt = Utils.marshal(x);
            int unmarshalledInt = Utils.unmarshal(marshalledInt, 0);
            System.out.println(marshalledInt);
            System.out.println(unmarshalledInt);
        } else if (dataType == 2) {
            System.out.println("Enter a string");
            String s = sc.nextLine();
            byte[] marshalledString = Utils.marshal(s);
            String unmarshalledString = Utils.unmarshal(marshalledString, 0, s.length());
            System.out.println(marshalledString);
            System.out.println(unmarshalledString);
        }

        sc.close();
    }
}