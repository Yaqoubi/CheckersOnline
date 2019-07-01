package User;

import java.io.*;
import java.net.Socket;

public class User {
  //jaryan vorodi va khoroji
  private PrintStream outStream;
  private InputStream inStram;
  
  //khasise haye user az jomle esm va socket
  private String name;
  private Socket client;
  private String password;

  //constructor ijad karbar jadid
  public User(Socket socket, String name,String password) throws IOException {
    if(socket != null){
        this.outStream = new PrintStream(socket.getOutputStream());
        this.inStram = socket.getInputStream();
    }
    this.client = client;
    this.name = name;
    this.password = password;
  }

  public PrintStream getOutStream(){
    return this.outStream;
  }

  public InputStream getInputStream(){
    return this.inStram;
  }

  public String getName(){
    return this.name;
  }
  
  public String getPassword(){
    return this.password;
  }
  
  public String toString(){
    return this.getName();
  }
}