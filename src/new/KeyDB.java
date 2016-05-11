/* This is were the Server stores the authorized devices ID's and public key's. */

package IoT;

class KeyDB {

  private KeyDB kdb = new KeyDB(new String[]{"prova1","prova2"}, new int[]{45,46});
  private String[] IDs = new String[10];
  private int[] keys = new int[10];

  private KeyDB(String[] ids, int[] val) {
    for(int i=0; i<ids.length; i++){
      this.IDs[i] = ids[i];
      this.keys[i] = val[i];
    }
  }
}
