package pt.ulisboa.tecnico.client.utils.transport;

public interface Responsible {
    public void handleMessage(String data);
    public int getId();
    public int getPort();
    public String getAddress();
}
