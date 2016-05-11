/* This thread is the one who manages new devices.
  At the beginning, security protocol is executed. If device can be authenticated,
  then the execution continue with requests and responses management, if not the connection is closed and the thread
  stop his execution. */

package IoT;

public class IotManager {

  private Socket device;

  public void run() {

  }
}
