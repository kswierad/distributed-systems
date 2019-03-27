import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MergeView;
import org.jgroups.View;

import java.util.Comparator;
import java.util.LinkedList;
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
        LinkedList<View> subgroups=new LinkedList<>(view.getSubgroups());
        Address local_addr = ch.getAddress();
        subgroups.sort(Comparator.comparingInt(View::size));
        View tmp_view = subgroups.get(subgroups.size()-1);
        if(!tmp_view.getMembers().contains(local_addr)) {
            System.out.println("Not member of the new primary partition ("
                    + tmp_view + "), will re-acquire the state");
            try {
                ch.getState(null, 30000);
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