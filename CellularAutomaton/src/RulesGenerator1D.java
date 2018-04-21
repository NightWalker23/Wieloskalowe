import java.util.Arrays;

public class RulesGenerator1D {
    private int[] tab;
    private int[] rules;
    private int ruleNumber;
    public final int type;
    public static int NORMAL = 1;
    public static int PERIODIC = 2;

    public RulesGenerator1D( int size, int ruleNumber, int type ) {
        this.type = type;
        this.ruleNumber = ruleNumber;
        tab = new int[size];
        Arrays.fill( tab, 0 );
        tab[size / 2] = 1;
        rules = new int[8];

        for( int i = 0; i < 8; i++ )
            rules[8 - i - 1] = getBit( i );
    }

    private int getBit( int position ) {
        return ( ruleNumber >> position ) & 1;
    }

    private int checkNeighbours( int[] t, int index ) {
        int result = 0;
        int before, that = t[index], after;

        if( ( index != 0 && index != ( t.length - 1 ) ) || type == RulesGenerator1D.PERIODIC ) {

            if( type == RulesGenerator1D.PERIODIC && index == 0 ) before = t[t.length - 1];
            else before = t[index - 1];

            if( type == RulesGenerator1D.PERIODIC && index == t.length - 1 ) after = t[0];
            else after = t[index + 1];

            if( before == 1 && that == 1 && after == 1 ) result = rules[0];
            else if( before == 1 && that == 1 && after == 0 ) result = rules[1];
            else if( before == 1 && that == 0 && after == 1 ) result = rules[2];
            else if( before == 1 && that == 0 && after == 0 ) result = rules[3];
            else if( before == 0 && that == 1 && after == 1 ) result = rules[4];
            else if( before == 0 && that == 1 && after == 0 ) result = rules[5];
            else if( before == 0 && that == 0 && after == 1 ) result = rules[6];
            else if( before == 0 && that == 0 && after == 0 ) result = rules[7];
        }
        else if( type == RulesGenerator1D.NORMAL ) {
            if( index == 0 ) {
                after = t[index + 1];
                if( that == 1 && after == 1 ) result = rules[4];
                else if( that == 1 && after == 0 ) result = rules[5];
                else if( that == 0 && after == 1 ) result = rules[6];
                else if( that == 0 && after == 0 ) result = rules[7];
            }
            else if( index == t.length - 1 ) {
                before = t[index - 1];
                if( before == 1 && that == 1 ) result = rules[1];
                else if( before == 1 && that == 0 ) result = rules[3];
                else if( before == 0 && that == 1 ) result = rules[5];
                else if( before == 0 && that == 0 ) result = rules[7];
            }
        }

        return result;
    }

    public int[] getTab() {
        int[] tmp = new int[tab.length];
        System.arraycopy( tab, 0, tmp, 0, tab.length );
        return tmp;
    }

    public int[] getResult( int[] t ) {
        for( int i = 0; i < t.length; i++ )
            tab[i] = checkNeighbours( t, i );

        return getTab();
    }

}
