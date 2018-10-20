import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author gaffn
 *
 */
public class Nagaffne_hw1_client {
  /*Encrypt the username sent over the connection
   * @param plainText is the plain text username to be encrypted
   * */
  public static String encryptData(String plainText){
    int shiftnum = 2; //This is the number of shifts to be made.
    StringBuilder cipherText = new StringBuilder(plainText); //Create a string of the text to be created
    String plain = "abcdefghijklmnopqrstuvwxyz";  //This is the original unaltered alphabet
    StringBuilder cipherKey = new StringBuilder(plain); //Create a Stringbuilder from set of characters to use
    cipherKey.append(cipherKey.substring(0,shiftnum)); //Takes the first shiftnum characters and add them to end
    cipherKey.delete(0,shiftnum); //Remove the duplicates of the chars appended to end
    //System.out.println(cipherKey.toString());
    boolean charFound = false;
    int charIndex = 0;
    for(int i=0; i < cipherText.length(); i++){
        charIndex = 0; //Location of the checked character in library
        charFound = false;
        while (!charFound){
          /* plain.charAt(charIndex) is the character from the library that will indicate which
           * value the text shoudl be shifted to
           * */
            if(plain.charAt(charIndex) == Character.toLowerCase(cipherText.charAt(i)) || cipherText.charAt(i) == ' '){
                //System.out.println("1" + plain.charAt(charIndex));
                //System.out.println("2" + plain.charAt(cipherText.charAt(i)));
                charFound = true;
                //System.out.println("Found: " + plain.charAt(charIndex));
            }
            
            else{
              charIndex++;
            }
            
        }
        if(cipherText.charAt(i)!= ' '){
            Character.toLowerCase(cipherText.charAt(i)); //Make the character to be changed lower case
            /*Change the character at i to the value of the shifted alphabet of value charIndex
             * If the original character was the 6th index of the library then the new character will
             * be the 6th character of the shifted library
             * */
            cipherText.setCharAt(i,cipherKey.charAt(charIndex)); 
            
        }
    }
    
    return cipherText.toString();
}
  
  
  
  public static String decryptData(String cryptText){
    int shiftnum = 2; //This is the number of shifts to be made.
    StringBuilder cipherText = new StringBuilder(cryptText);
    String plain = "0123456789";  //The library needs to change as client receives numerical data
    boolean charFound = false;
    int charIndex = 0;
    for(int i=0; i < cipherText.length(); i++){
      charIndex = 0;
      charFound = false;
      while (!charFound){
        if(cipherText.charAt(i) == plain.charAt(charIndex) || cipherText.charAt(i) == ' '){
          //System.out.println("1" + plain.charAt(charIndex));
          charFound = true;
          //System.out.println("Found: " + plain.charAt(charIndex));
        }
        
        else{
          charIndex++;
          //System.out.println("charIndex: " + charIndex);
        }
        
      }
      if(cipherText.charAt(i)!= ' '){
        //Character.toLowerCase(cipherText.charAt(charIndex));
        if(charIndex-shiftnum>=0) {
        cipherText.setCharAt(i,plain.charAt(charIndex-shiftnum));
        }
        else {
          cipherText.setCharAt(i,plain.charAt(charIndex-shiftnum+(plain.length())));
        }
        //System.out.println(plain.charAt(charIndex-shiftnum));
      }
    }
    
    return cipherText.toString();
  }
  
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	  
	  
	  
	  String firstName = "";
    String lastName = "";
    
    System.out.print("Enter first name: ");
    Scanner inText = new Scanner(System.in);
    firstName = inText.next();
    System.out.print("Enter last name: ");
    lastName = inText.next();
    

    inText.close();
    String outgoing = new String(encryptData(firstName+lastName)); //Encrypt the data and store in outgoing
    System.out.println("Retrieving information.");
    System.out.println("______________________");
    //~~~~~~~~~~~~~~~~~~~~~~~~~~
    try {
      String serverName;
      DatagramSocket socket;
      byte[] msg = new byte[1000];
      int servport = 3000;
      socket = new DatagramSocket(3001);
      serverName = "127.0.0.1";     //Ipaddress of the server
      DatagramPacket cipherTextSend  //Create a packet to carrry the encrypted packet
      = new DatagramPacket(outgoing.getBytes(), outgoing.length(), InetAddress.getByName(serverName), servport);
      socket.send(cipherTextSend);
      System.out.print("Sending:" + outgoing + "\n");
      
      //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      DatagramPacket cipherTextRecieve = new DatagramPacket(msg, msg.length);
      socket.receive(cipherTextRecieve); //Receive the encrypted ssn
      String received 
      = new String(cipherTextRecieve.getData(), 0, cipherTextRecieve.getLength());
      String plainTextReceieve = new String(decryptData(received)); //Decrypt the ssn and store in variable
      System.out.println("Received: " + received);
      System.out.println("______________________");
      System.out.println("Decrypted message: " + plainTextReceieve);
      
      
      
      
      //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      String out2= "Thank you.";
      DatagramPacket secpacket 
      = new DatagramPacket(out2.getBytes(), out2.length(), InetAddress.getByName(serverName), servport);
      socket.send(secpacket);
      System.out.println("Sending:" + out2);
      
      socket.close();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
	}

}
