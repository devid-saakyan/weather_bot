import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;

public class Main {

    HashMap<Integer,String> dictionary1 = new HashMap<>();


    public void Append(Integer key, String status){
        dictionary1.put(key,status);
    }

    public void Delete(Integer key, String status){
        dictionary1.remove(key);
    }

    public Boolean Checker(Integer key){
        return dictionary1.containsKey(key);
    }
}
