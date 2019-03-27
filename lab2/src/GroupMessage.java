import java.io.Serializable;

public class GroupMessage implements Serializable {


    public final OperationType type;
    public final String key;
    public final Integer value;

    private GroupMessage(OperationType type, String key, Integer value){
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public static GroupMessage  makeRemoveMessage(String key){
        return new GroupMessage(OperationType.REMOVE, key, null);
    }

    public static GroupMessage makePutMessage(String key, Integer value){
        return new GroupMessage(OperationType.PUT, key, value);
    }

    @Override
    public String toString() {
        return "GroupMessage{" +
                "type=" + type +
                ", key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

}
