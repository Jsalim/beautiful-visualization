package es.tid.haewoon.cdr.util;


public class Transition {
    public String cur;
    public String next;
    
    public Transition(String cur, String next) {
        this.cur = cur;
        this.next = next;
    }
    
    public String toString() {
        return cur + "\t" + next;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        Transition t1 = (Transition) obj;
        
        return this.cur.equals(t1.cur) && this.next.equals(t1.next);
    }

    @Override
    public int hashCode() {
        return (cur + next).hashCode();
    }
}
