import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception{

            DistributedMap map = new DistributedMap();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {

                String operation = scanner.next();
                String key;
                Integer value;
                switch (operation.toLowerCase()) {
                    case "add":
                        key = scanner.next();
                        value = scanner.nextInt();
                        map.put(key, value);
                        break;
                    case "delete":
                        key = scanner.next();
                        value = map.remove(key);
                        System.out.println("Deleted: " + value);
                        break;
                    case "all":
                        System.out.println(map);
                        break;
                    case "contains":
                        key = scanner.next();
                        boolean containsKey = map.containsKey(key);
                        if (containsKey)
                            System.out.println(key + " is in the map and has value: " + map.get(key));
                        else
                            System.out.println(key + " is not in the map");
                        break;
                    case "get":
                        key = scanner.next();
                        System.out.println("Under " + key + " is " + map.get(key));
                        break;
                    case "exit":
                        map.close();
                        return;
                    case "disconnect":
                        map.close();
                        break;
                    case "reconnect":
                        map.reconnect();
                        break;
                    default:
                        System.out.println("Command " + operation + " is not recognized");
                        break;
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }


}
