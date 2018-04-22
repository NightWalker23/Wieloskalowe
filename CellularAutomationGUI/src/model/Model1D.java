package model;

import java.util.Arrays;

public class Model1D {
    private int[] tab;
    private int[] rules;
    private int ruleNumber;
    public final int type;

    public static class Type {
        public static int NORMAL = 1;
        public static int PERIODIC = 2;
    }

    public static class Option {
        public static int DEAD = 0;
        public static int ALIVE = 1;
    }

    public Model1D(int size, int ruleNumber, int type) {
        this.type = type;
        this.ruleNumber = ruleNumber;
        tab = new int[size];
        Arrays.fill(tab, 0);
        tab[size / 2] = 1;
        rules = new int[8];

        for (int i = 0; i < 8; i++)
            rules[8 - i - 1] = getBit(i);
    }

    private int getBit(int position) {
        return (ruleNumber >> position) & 1;
    }

    private int checkNeighbours(int[] t, int index) {
        int result = 0;
        int prev, that = t[index], next;

        if ((index != 0 && index != (t.length - 1)) || type == Type.PERIODIC) {

            if (type == Type.PERIODIC && index == 0) prev = t[t.length - 1];
            else prev = t[index - 1];

            if (type == Type.PERIODIC && index == t.length - 1) next = t[0];
            else next = t[index + 1];

            if (prev == Option.ALIVE && that == Option.ALIVE && next == Option.ALIVE) result = rules[0];
            else if (prev == Option.ALIVE && that == Option.ALIVE && next == Option.DEAD) result = rules[1];
            else if (prev == Option.ALIVE && that == Option.DEAD && next == Option.ALIVE) result = rules[2];
            else if (prev == Option.ALIVE && that == Option.DEAD && next == Option.DEAD) result = rules[3];
            else if (prev == Option.DEAD && that == Option.ALIVE && next == Option.ALIVE) result = rules[4];
            else if (prev == Option.DEAD && that == Option.ALIVE && next == Option.DEAD) result = rules[5];
            else if (prev == Option.DEAD && that == Option.DEAD && next == Option.ALIVE) result = rules[6];
            else if (prev == Option.DEAD && that == Option.DEAD && next == Option.DEAD) result = rules[7];
        } else if (type == Type.NORMAL) {
            if (index == 0) {
                next = t[index + 1];
                if (that == Option.ALIVE && next == Option.ALIVE) result = rules[4];
                else if (that == Option.ALIVE && next == Option.DEAD) result = rules[5];
                else if (that == Option.DEAD && next == Option.ALIVE) result = rules[6];
                else if (that == Option.DEAD && next == Option.DEAD) result = rules[7];
            } else if (index == t.length - 1) {
                prev = t[index - 1];
                if (prev == Option.ALIVE && that == Option.ALIVE) result = rules[1];
                else if (prev == Option.ALIVE && that == Option.DEAD) result = rules[3];
                else if (prev == Option.DEAD && that == Option.ALIVE) result = rules[5];
                else if (prev == Option.DEAD && that == Option.DEAD) result = rules[7];
            }
        }

        return result;
    }

    public int[] getTab() {
        int[] tmp = new int[tab.length];
        System.arraycopy(tab, 0, tmp, 0, tab.length);
        return tmp;
    }

    public int[] getResult(int[] t) {
        for (int i = 0; i < t.length; i++)
            tab[i] = checkNeighbours(t, i);

        return getTab();
    }

}

