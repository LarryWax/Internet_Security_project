/* This is the main execution point.
  Two sockets are started, one listening for new devices, the other listening for new external requests.
  Every new client is managed by a new thread. There are two different types of threads: one who manages devices,
  the other who manages clients.
  Messages are exchanged between threads by server mediator methods. */

package IoT;

public class ConcreteServer implements ServerInterface {

  private String buffer;
  private boolean bufferIsEmpty;

  public synchronized void storeMessage(String msg) {
    while(!bufferIsEmpty) {
      try {
        wait();
      } catch (InterruptedException e) {
				e.printStackTrace();
			}
    }

    bufferIsEmpty = false;
    buffer = msg;
    notifyAll();
  }

  public synchronized String retriveMessage() {
    while(bufferIsEmpty) {
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    bufferIsEmpty = true;
    notifyAll();
    return buffer;
  }

}
