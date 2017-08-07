package controller;

public class TestCode {

    private int x;
    private int y;
    
    public TestCode(int x, int y) {
        this.x = x;
        this.y = x * y;
    }
    
    public void calc(boolean b) {
        if(b) {
            x = 3;
        } else {
            x = 5;
        }
    }
    
}
