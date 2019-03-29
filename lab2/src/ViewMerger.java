import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MergeView;
import org.jgroups.View;

import java.util.LinkedList;


public class ViewMerger extends Thread {
    JChannel ch;
    MergeView view;

    public ViewMerger(JChannel ch, MergeView view) {
        this.ch=ch;
        this.view=view;
    }

    public void run() {
        LinkedList<View> subgroups=new LinkedList<>(view.getSubgroups());
        Address local_addr = ch.getAddress();
        View tmp_view = subgroups.getFirst();
        if(!tmp_view.getMembers().contains(local_addr)) {
            System.out.println("Not member of the new primary partition ("
                    + tmp_view + "), will re-acquire the state");
            try {
                ch.getState(null, 0);
                System.out.println("Merged succesfully");
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            System.out.println("Member of the new primary partition ("
                    + tmp_view + "), will do nothing");
        }
    }
}