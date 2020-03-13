package com.example;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class BookStore extends UnicastRemoteObject implements RmiInterface{
    private static final long serialVersionUID = 1234;

    private List<Book> bookList;

    protected BookStore(List<Book> list) throws RemoteException {
        super();
        this.bookList = list;
    }
    @Override
    public Book findBook(Book b) throws RemoteException{
        return bookList.stream().filter(o -> o.getTitle().equals(b.getTitle())).findFirst().get();
    }

    @Override
    public List<Book> allBooks() throws RemoteException{
        return bookList;
    }

    private static List<Book> initializeList() {
        List<Book> list = new ArrayList<>();
        list.add(new Book("H"));
        list.add(new Book("Java In A Nutshell"));
        list.add(new Book("Java: The Complete Reference"));
        list.add(new Book("Head First Servlets and JSP"));
        list.add(new Book("SJ"));
        return list;
    }


    public static void main(String[] args) {
        try {
            Naming.rebind("//localhost/MyBookstore", new BookStore(initializeList()));
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }


}
