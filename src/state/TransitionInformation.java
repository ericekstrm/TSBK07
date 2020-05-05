package state;

import java.util.HashMap;
import java.util.Map;

public class TransitionInformation
{

    String prevState;

    private Map<String, String> info = new HashMap<>();

    public TransitionInformation(String prevState)
    {
        this.prevState = prevState;
    }

    public void put(String key, String value)
    {
        info.put(key, value);
    }

    public String get(String key)
    {
        if (info.containsKey(key))
        {
            return info.get(key);
        } else 
        {
            return "";
        }
    }
}
