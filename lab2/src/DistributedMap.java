import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class DistributedMap implements SimpleStringMap {

    private static String NAME = "SWIERAD_GROUP";
    private Map<String, Integer> map = new HashMap<>();
    private JChannel channel;
    public DistributedMap()throws Exception{
        initChannel(NAME, "230.100.200.25");
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public Integer get(String key) {
        return map.get(key);
    }

    @Override
    public void put(String key, Integer value) {

        map.put(key, value);
        sendMessage(GroupMessage.makePutMessage(key, value));
    }

    @Override
    public Integer remove(String key) {
        Integer tmp= map.remove(key);
        sendMessage(GroupMessage.makeRemoveMessage(key));
        return tmp;
    }

    private void sendMessage(GroupMessage message){
        try {
            byte[] buffer = Util.objectToByteBuffer(message);
            Message jMessage = new Message(null, buffer);
            channel.send(jMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        channel.close();
    }

    @Override
    public String toString() {
        StringBuilder stringMap = new StringBuilder();
        for (String key : map.keySet()) {
            stringMap.append(key + " -> ").append(map.get(key)).append("\n");
        }

        return stringMap.toString();
    }

    public void initChannel(String name, String address) throws Exception{
        System.setProperty("java.net.preferIPv4Stack", "true");

        channel = new JChannel(false);
        ReceiverAdapter adapter = new DistributedMapAdapter(map, channel);

        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);

        UDP udp = new UDP();
        udp.setValue("mcast_group_addr", InetAddress.getByName(address));
        stack.addProtocol(udp)
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE())
                .addProtocol(new FLUSH());

        stack.init();

        channel.setReceiver(adapter);

        channel.connect(name, null, 0);
    }
    public void reconnect() throws Exception{
        channel.connect(NAME, null, 0);
    }
}
