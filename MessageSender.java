
import java.lang.String; 

/**
 * This class implements the sender side of the data link layer.
 

public class MessageSender
{
    // Fields ----------------------------------------------------------

    private int mtu;                    // maximum transfer unit (frame length limit)
    private FrameSender physicalLayer;  // physical layer object
    private TerminalStream terminal;    // terminal stream manager
        

    
    // Constructor -----------------------------------------------------

    /**
     * MessageSender constructor
     * Create and initialize new MessageSender.
     * @param mtu the maximum transfer unit (MTU)
     * (the length of a frame must not exceed the MTU)
     * @throws ProtocolException if error detected
     */

    public MessageSender(int mtu) throws ProtocolException
    {
        // Initialize fields
        // Create physical layer and terminal stream manager
        this.mtu = mtu;
        this.physicalLayer = new FrameSender();
        this.terminal = new TerminalStream("MessageSender");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
        
        
    }

    // Methods ---------------------------------------------------------

    /**
     * Send a single message 
     * @param message the message to be sent.  The message can be any
     * length and may be empty but the string reference should not
     * be null.
     * @throws ProtocolException immediately without attempting to
     * send any further frames if, and only if, the physical layer
     * throws an exception or the given message can't be sent
     * without breaking the rules of the protocol including the MTU
     */
       
    public void sendMessage(String message) throws ProtocolException 
    {
        // Report action to terminal
        // Note the terminal messages aren't part of the protocol,
        // they're just included to help with testing and debugging

        terminal.printlnDiag("  sendMessage starting (message = \"" + message + "\")");

      
        
        
       //Following two lines .
       String colonDoubleLong = ":"; 
       message = message.replace(colonDoubleLong, colonDoubleLong+colonDoubleLong); 
           
       //LONG MESSAGE FRAME CREATOR
       while(message.length() + 8 > mtu) { //While the message + 8 is longer than the MTU the message is split. (8 is the number of the other components in the frame)
           //Split a message so that its not longer than the MTU. (Only currently works with an MTU of 18.
           // IDEA - substring 2 is mtu -1 and then an if statement to check if its in bounds
           int substring1 = 0; 
           int substring2 = 10; 
           String holdmessage = ""; 
           holdmessage = message.substring(substring1, substring2);  //Makes a message within the MTU 
         
           
           //Sorts out the colon spacing in a message so they stay togeather (Not sure if this works in all cases)
           int lastCharIsColon = 9;
           while(holdmessage.substring(holdmessage.length() -1, holdmessage.length() ).contains(":")) { 
                         holdmessage = holdmessage.substring(substring1,   lastCharIsColon);
                         lastCharIsColon = lastCharIsColon - 1;
                         StringBuffer addMissingColon = new StringBuffer(message);
                         message = addMissingColon.insert(10, ":").toString(); //add a colon back to message(first charecter)
            } 
             
           //Calculate the CHECKSUM 
           int count = 0;
           int Checksum = 0;
           holdmessage = holdmessage.replace(colonDoubleLong+colonDoubleLong, colonDoubleLong); //Remove the colons so they don't get calculated. 
           for(int i = 0; i < holdmessage.length() ; i++) { //Loop through the message and calculate the checksum
            count = holdmessage.charAt(i);   
            Checksum = Checksum + count;        
            
           } 
           holdmessage = holdmessage.replace(colonDoubleLong, colonDoubleLong+colonDoubleLong); //Add the colons back in after calculation. 
           
           //Calculate if the frame requires a + or a .
           String PlusOrDot = ".";
           if(message.length() + 8 >= mtu) {       //Check if its going to be a + or a . 
           PlusOrDot = "+";                           
           }
           
           //If the message is empty or null print this type of frame
           String checksumZero = "000"; 
           if(message == null || message.length() == 0) { //if the message is null or empty.
           physicalLayer.sendFrame("(" + holdmessage + ":" + checksumZero + ":" + PlusOrDot + ")"); //print to the terminal with the checksum as 000
           } 
           
           //If the Checksum is less than 10 padd and print this type of frame
           //Shouldnt really need to padd all of this seperatly can be done in one method.
           String checksumLessThan10 = "";
           if(Checksum < 10) {
           checksumLessThan10 = checksumLessThan10.format("%03d", Checksum);
           physicalLayer.sendFrame("(" + holdmessage + ":" + checksumLessThan10 + ":" + PlusOrDot + ")");
           }
        
           //If the Chechsum is less than 100 but more than 9 print this type of frame
           String checksumLessThan100 = "";
           if(Checksum < 100 & Checksum > 9) { 
           checksumLessThan100 = checksumLessThan100.format("%03d", Checksum);
           physicalLayer.sendFrame("(" + holdmessage + ":" + checksumLessThan100 + ":" + PlusOrDot + ")");
           }
       
           //If the Checksum is more than 999 print this type of frame.
           String checksumMoreThan999 = "";
           if(Checksum > 999) { 
           checksumMoreThan999 = checksumMoreThan999.valueOf(Checksum).substring(checksumMoreThan999.valueOf(Checksum).length()-3);
           physicalLayer.sendFrame("(" + holdmessage + ":" + checksumMoreThan999 + ":" + PlusOrDot + ")"); 
           }
           
           //If the Checksum is more than 99 but less than 1000 print this type of frame.
           if(Checksum > 99 & Checksum < 1000) {
      
           physicalLayer.sendFrame("(" + holdmessage + ":" + Checksum + ":" + PlusOrDot + ")"); //print to the terminal normally
    
           }
           
           message = message.substring(substring2, message.length()); //message = last part of sub string
          
           //message.length - mtu, mtu -1
           //original substring2, message.length()
        }
        
       //SHORT MESSAGE 
        
       // CALCULATE CHECKSUM
       int count = 0;
       int Checksum = 0;
       message = message.replace(colonDoubleLong+colonDoubleLong, colonDoubleLong); //Remove the colons to calculate checksum correctly
       for(int i = 0; i < message.length() ; i++) { 
            count = message.charAt(i);   
            Checksum = Checksum + count; 
            }
       message = message.replace(colonDoubleLong, colonDoubleLong+colonDoubleLong); //Add the colons back in to form a correct frame
   
       //Assign the correct + or . for the frame.
       String PlusOrDot = ".";
       if(message.length() + 8 > mtu) { 
          PlusOrDot = "+";
        }
       
      
       //If the message is empty or null print this type of frame
       String checksumZero = "000"; //holds 000 incase the message is null or empty. 
       if(message == null || message.length() == 0) { //if the message is null or empty.
       physicalLayer.sendFrame("(" + message + ":" + checksumZero + ":" + PlusOrDot + ")"); //print to the terminal with the checksum as 000
        } 
       else {
       //If the Checksum is less than 10 padd and print this type of frame.
       String checksumLessThan10 = "";
       if(Checksum < 10) {
         checksumLessThan10 = checksumLessThan10.format("%03d", Checksum);
         physicalLayer.sendFrame("(" + message + ":" + checksumLessThan10 + ":" + PlusOrDot + ")");
        }
       //If the Checksum is less than 100 and more than 9 padd and print this type of frame.
       String checksumLessThan100 = "";
       if(Checksum < 100 & Checksum > 9) { 
          checksumLessThan100 = checksumLessThan100.format("%03d", Checksum);
          physicalLayer.sendFrame("(" + message + ":" + checksumLessThan100 + ":" + PlusOrDot + ")");
        }
       //If the Checksum is more than 999 padd and print this type of frame.
       String checksumMoreThan999 = "";
       if(Checksum > 999) { 
        checksumMoreThan999 = checksumMoreThan999.valueOf(Checksum).substring(checksumMoreThan999.valueOf(Checksum).length()-3);
        physicalLayer.sendFrame("(" + message + ":" + checksumMoreThan999 + ":" + PlusOrDot + ")"); 
        }
       //If the Checksum is more than 99 and less than 1000 padd and print this type of frame.
       if(Checksum > 99 & Checksum < 1000) {
      
        physicalLayer.sendFrame("(" + message + ":" + Checksum + ":" + PlusOrDot + ")"); //print to the terminal normally
    
       }}
      
    
 
       // Report completion of task

       terminal.printlnDiag("  sendMessage finished");

    } // end of method sendMessage
     
   
    
} // end of class MessageSender

