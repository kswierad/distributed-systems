import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MergeView;
import org.jgroups.View;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class ViewMerger extends Thread {
    JChannel ch;
    MergeView view;

    public ViewMerger(JChannel ch, MergeView view) {
        this.ch=ch;
        this.view=view;
    }

    public void run() {
        List<View> subgroups=view.getSubgroups();
        Address local_addr = ch.getAddress();
        subgroups.sort(Comparator.comparingInt(View::size));
        View tmp_view = subgroups.get(0);
        if(!tmp_view.getMembers().contains(local_addr)) {
            System.out.println("Not member of the new primary partition ("
                    + tmp_view + "), will re-acquire the state");
            try {
                ch.getState(null, 30000);
            }
            catch(Exception ex) {
            }
        }
        else {
            System.out.println("Member of the new primary partition ("
                    + tmp_view + "), will do nothing");
        }
    }
}