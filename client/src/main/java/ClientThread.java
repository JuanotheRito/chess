import client.Repl;

public class ClientThread extends Thread{
    String serverUrl;
    ClientThread(String serverUrl){
        this.serverUrl = serverUrl;
    }
    @Override
    public void run() {
        new Repl(serverUrl).run();
    }
}
