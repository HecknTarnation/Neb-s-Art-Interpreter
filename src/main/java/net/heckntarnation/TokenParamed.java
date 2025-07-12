package net.heckntarnation;

import java.util.Arrays;

public class TokenParamed {

    public Main.Token token;
    public String[] params;

    public TokenParamed(Main.Token token){
        this.token = token;
        this.params = new String[3];
    }

    public TokenParamed setParam(int index, String param){
        this.params[index] = param;
        return this;
    }

    @Override
    public String toString(){
        return this.token + ": " + Arrays.toString(params);
    }
}
