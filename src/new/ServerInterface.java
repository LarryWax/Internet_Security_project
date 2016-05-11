/* This is the interface of the mediator, it describes methods Server will
   use to handle clients requests and devices responses.
   It will work between different threads. */
   
package IoT;

public interface ServerInterface {

  void storeMessage(String msg);
  String retriveMessage();
}
