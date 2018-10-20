/**
 * 
 */
//package nagaffne_hw1;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
/**
 * @author gaffn
 *
 */
public class Nagaffne_hw1_server {
  /*
   * This is the server version of encrypt data, it is designed to encrypt number based strings
   * The only change is the plain string which is the library of characters
   * */
  public static String encryptData(String plainText){
    int shiftnum = 2; //This is the number of shifts to be made.
    StringBuilder cipherText = new StringBuilder(plainText);
    //The server needs to encrypt numerical strings, as such the library must contain numbers
    String plain = "0123456789";
    StringBuilder cipherKey = new StringBuilder(plain); //Create a Stringbuilder from set of characters to use
    cipherKey.append(cipherKey.substring(0,shiftnum)); //Takes the first shiftnum characters and add them to end
    cipherKey.delete(0,shiftnum); //Remove the duplicates of the chars appended to end
    //System.out.println(cipherKey.toString());
    boolean charFound = false;
    int charIndex = 0;
    for(int i=0; i < cipherText.length(); i++){
        charIndex = 0;
        charFound = false;
        while (!charFound){
            if(plain.charAt(charIndex) == Character.toLowerCase(cipherText.charAt(i)) || cipherText.charAt(i) == ' '){
                //System.out.println("1" + plain.charAt(charIndex));
                //System.out.println("2" + plain.charAt(cipherText.charAt(i)));
                charFound = true;
                //System.out.println("Found: " + plain.charAt(charIndex));
            }
            
            else{
              charIndex++;
              //System.out.println("charIndex: " + charIndex);
            }
            
        }
        if(cipherText.charAt(i)!= ' '){
            Character.toLowerCase(cipherText.charAt(i));
            cipherText.setCharAt(i,cipherKey.charAt(charIndex));
            //System.out.println(cipherText);
        }
    }
    
    return cipherText.toString();
}
  /* @param cryptText This is the encrypted text received from the client
   * @return A result of plain text will be returned 
   * */
  public static String decryptData(String cryptText){
    int shiftnum = 2; //This is the number of shifts to be made.
    StringBuilder cipherText = new StringBuilder(cryptText);
    String plain = "abcdefghijklmnopqrstuvwxyz";    //The alphabet to be searched
    boolean charFound = false; //Flag to stop the while loop, will search through the plain 
                               //alphabet to find match
    int charIndex = 0;         //Location of the value found in the alphabet
    for(int i=0; i < cipherText.length(); i++){
      charIndex = 0; //Reset location of the found value
      charFound = false;
      /*Loop through the encrypted text matching chars until one is found in the original alphabet
       * record the index location of the values
       */
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
      if(cipherText.charAt(i)!= ' '){ //Leave spaces alone
        if(charIndex-shiftnum>=0) {
        //If the found index value is not the first shiftnum then replace the
        //char at index i with the letter shiftnum spaces left of the found charIndex
        cipherText.setCharAt(i,plain.charAt(charIndex-shiftnum));
        }
        else {
          //If the found value is at the end of the alphabet then the resulting index will be negative
          //Adding the length of the alphabet will produce the correct positive index location in the alphabet
          cipherText.setCharAt(i,plain.charAt(charIndex-shiftnum+(plain.length())));
        }
        //System.out.println(plain.charAt(charIndex-shiftnum));
      }
    }
    
    return cipherText.toString();
  }
  /*Search the text file for the record sent by client
   * */
  private static String findRecord(String username) throws Exception {
    System.out.println("Looking for record for " + username);
    String foundvalue = "";
    boolean found = false;    //Flag to determine when to stop searching
    String[] arraryOfStringFromLine; //Will hold the split strings based on the delimieter
    /*The file to be opened is in the current directory*/
    File file = new File(System.getProperty("user.dir")+"\\logininfo.txt");
    BufferedReader br = new BufferedReader(new FileReader(file));
    String fileLine; 
    /*Loop through the file comparing the username of each line to the user sent from client*/
    while ((fileLine = br.readLine()) != null && found != true) {
      arraryOfStringFromLine = fileLine.split(",",2); //Breaks the string into two tokens (username,ssn)
      if(arraryOfStringFromLine[0].equals(username)) {
        found = true;
        foundvalue=arraryOfStringFromLine[1]; //set foundvalue to the ssn found in file
      }
    }
    if(!found) {
      //Acknowledge the client even if record not found
      foundvalue = "9999999999";
    }
      //System.out.println(fileLine); 
    br.close();
    return foundvalue;
  }
  
	/**
	 * @param args
	 */
	public static void main(String[] args) {	  
	  //Set the port number for the server
	  try {
	    
	    DatagramSocket socket;
	    DatagramPacket inPacket, outPacket;
      socket = new DatagramSocket(3000); //Binds the socket to port 3000
      byte[] inByte = new byte[1000];
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  System.out.println("Server is waiting for packet.\n");
	  
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  /*This block will receive encrypted data from teh client
	   * */
	  inPacket = new DatagramPacket(inByte, inByte.length);
	  socket.receive(inPacket);                                          //Receieve the encrypted data in packet form
	  String receivedEncrypted                                           //Convert packet byte data to a string
    = new String(inPacket.getData(), 0, inPacket.getLength());
	  System.out.println("Server received encrypted data:" + receivedEncrypted);
	  String received = decryptData(receivedEncrypted);                  //Decrypt the string
	  System.out.println("Server decrypted:" + received);
	  
	  //~~~~~~~
	  
	  /*Prepare the message (ssn) to be sent to the client
	   * */
	  String temp = "9999999999";
	  //String temp2 = "111236378";
	  temp = findRecord(received);
    System.out.println("Server selected record " + received + ": " + temp);
	  String outgoing = new String(encryptData(temp));
	  InetAddress ip = inPacket.getAddress(); //Get the client's ip address from the sent packet
	  int sendport = inPacket.getPort();      //Clients port number
	  outPacket = new DatagramPacket(outgoing.getBytes(), outgoing.length(), ip, sendport);
    socket.send(outPacket);
    
    
    
    inPacket = new DatagramPacket(inByte, inByte.length);
    socket.receive(inPacket);
    String closingMessage                         //Convert packet byte data to a string
    = new String(inPacket.getData(), 0, inPacket.getLength());
    System.out.println("Received: " + closingMessage);
    
	  socket.close();
	} 
	  catch (Exception ex) {
	    
	  }
	}

}

