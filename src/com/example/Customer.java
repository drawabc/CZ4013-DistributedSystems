package com.example;

import com.example.RmiInterface;
import com.example.BookStore;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Customer {
    private static RmiInterface look_up;

    public static void main(String[] args) throws
            MalformedURLException, RemoteException, NotBoundException {
        look_up = (RmiInterface) Naming.lookup("//localhost/MyBookstore");
        Scanner Sc = new Scanner(System.in);
        int choice = 1;
        boolean findmore = true;
        String bookName;
        do {
            System.out.println("1: Print all books");
            System.out.println("2: Find book books");
            choice = Sc.nextInt();
            switch(choice){
                case 1:
                    System.out.println(look_up.allBooks());
                    break;
                case 2:
                    System.out.println("What book?");
                    bookName = Sc.nextLine();
                    try{
                      System.out.println(look_up.findBook(new Book(bookName)));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    choice = 3;
                    break;
            }
        } while(choice!=3);
    }

}
