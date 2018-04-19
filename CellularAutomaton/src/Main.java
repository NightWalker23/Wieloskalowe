public class Main {

    public static void show( int[] tab ) {
        for( int i = 0; i < tab.length; i++ )
            if( tab[i] == 1 ) System.out.print( 1 + " " );
            else System.out.print( "  " );
        System.out.println();
    }

    public static void draw( RulesGenerator1D rule, int iterations ) {
        show( rule.getTab() );
        for( int i = 0; i < iterations - 1; i++ )
            show( rule.getResult( rule.getTab() ) );
    }

    public static void main( String[] args ) {
        RulesGenerator1D rule = new RulesGenerator1D( 41, 90);
        draw(rule, 15);
    }
}
