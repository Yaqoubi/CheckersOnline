package User;

import Chat.*;
import java.util.Scanner;

public class UserMessagesHandler implements Runnable {

  //Socket Server
  private Server server;
  //object user makhsose in handler
  private User user;

  public UserMessagesHandler(Server server, User user) {
    this.server = server;
    this.user = user;
    this.server.updateUserList();
  }
  //handle kardan payamhaye ersali karbar be server
  public void run() {
    String message;
    Scanner scanner = new Scanner(this.user.getInputStream());
    while (scanner.hasNextLine()) {
      //daryaft message
      message = scanner.nextLine();
      //User Decline Karde Request Ro Ersal In Ghaziye Be karbar darkhast dahande
      if(message.contains("!No")){
          String sendTo = message.substring(message.indexOf(" ")+1);
          server.sendToUser("?No", sendTo);
      }
      // (( be in mana ast ke payame karbar az daste payam haye khososi ast,client gui ham check shavad vase darkar bishtar
      // formate koli ((esm karbar ersal konande))esm karbar daryaft konande "yek space bades" payam
      //((ali))amir salam
      else if(message.contains("((")){
          String sendTo = message.substring(message.indexOf("))")+2,message.indexOf(" "));
          server.sendToAll(message);
      }
      // || be in mana ast ke payame karbar az daste payam haye game hast
      // formate koli ||esm karbar ersal konande//esm karbar daryaft konande "yek space bades" row va col start "yek space bades" row va col hadaf
      //||ali//amir salam
      else if(message.contains("||")){
          String sendTo = message.substring(message.indexOf("//")+2,message.indexOf(" "));
          server.sendToAll(message);
      //Handle request game va chat be user @username !chat ya @username !game
      }else if (message.charAt(0) == '@'){
        if(!message.contains("!")) {
          server.sendToAllHTMLFormat(message, user);
        }else{
            //Ersal Darkhast Chat Be User Morde Nazar
            if(message.contains("!Chat")){
                int firstSpace = message.indexOf(" ");
                String userPrivate= message.substring(1, firstSpace);
                server.sendToUser(
                "!Request Chat "+user, userPrivate
                );
            //Ersal Darkhast Game Be User Morde Nazar    
            }else if(message.contains("!Game")){
                int firstSpace = message.indexOf(" ");
                String userPrivate= message.substring(1, firstSpace);
                server.sendToUser(
                "!Request Game "+user, userPrivate
                );
            }
        }
      //Ersal inke user accept kard darkhaste chat ro
      }else if(message.contains("?ChatYes")){
          String userName = message.substring(8);
                server.sendToUser(
                "?Chat? "+user, userName
                );
      //Ersal inke user accept kard darkhaste game ro          
      }else if(message.contains("?GameYes")){
          String userName = message.substring(8);
                server.sendToUser(
                "?Game? "+user, userName
                );
      //dar gheyre sorat halat haye bala yani message ro dar global ferstade ast past bordcast be hame user ha          
      }else{
        server.sendToAllHTMLFormat(message, user);
      }
    }
    //hazf user va bordcast inke user disconnect shode ta har client az list khodesh remove kone
    server.removeUser(user);
    this.server.updateUserList();
    scanner.close();
  }
}