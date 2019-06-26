package eie.robot.com.common;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class mData {

    private static String[] ChatSpeaking = {
            "大家好","红包有吗","红包红包","红包勒","你们好啊","你们怎么刷金币啊","人勒","大笑大笑"
            ,"加油","有自信才有未来","害羞","点击没金币了","哈哈哈"
    };
    public  static String getRandomSChatSpeaking(){
        int randomIndex = (int) Math.floor(Math.random() * ChatSpeaking.length);
        if(randomIndex >= ChatSpeaking.length || randomIndex < 0){
            randomIndex = ChatSpeaking.length-1;
        }
        return ChatSpeaking[randomIndex];
    }

}
