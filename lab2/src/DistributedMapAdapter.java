import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DistributedMapAdapter extends ReceiverAdapter {

    private Map<String, Integer> map;
    private final JChannel channel;

    public DistributedMapAdapter(Map<String, Integer> map, JChannel channel){
        this.channel = channel;
        this.map = map;
    }

    @Override
    public void viewAccepted(View view){
        super.viewAccepted(view);
        System.out.println(view.toString());
        if(view instanceof MergeView) {
            MergeView tmp=(MergeView)view;
            ViewMerger merger = new ViewMerger(channel, tmp);
            merger.start();
        }
    }

    @Override
    public void receive(Message msg){
        try {
            GroupMessage groupMessage = (GroupMessage) Util.objectFromByteBuffer(msg.getBuffer());
            switch (groupMessage.type){
                case PUT:
                    map.put(groupMessage.key, groupMessage.value);
                    break;
                case REMOVE:
                    map.remove(groupMessage.key);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized(map) {
            Util.objectToStream(map, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        Map<String, Integer> tmp;
        tmp = (Map<String, Integer>) Util.objectFromStream(new DataInputStream(input));
        synchronized (map) {
            map.clear();
            map.putAll(tmp);
        }
    }
}
