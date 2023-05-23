package bguspl.set.ex;

import java.util.List;

public class Offer {
private List<Integer> Token;
private int playerId;

public Offer(List<Integer> token,int id){
    Token=token;
    playerId=id;
}
public List<Integer> GetToken(){
    return Token;

}
public int GetId(){
    return playerId;
}

    
}
