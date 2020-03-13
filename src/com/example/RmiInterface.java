package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RmiInterface extends Remote{
    Book findBook(Book b) throws RemoteException;
    List<Book> allBooks() throws RemoteException;
}
