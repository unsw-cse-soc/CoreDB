/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.sql;

/**
 *
 * @author vmc
 */

public class Q {
	// KEY WORDS

	public static String DISTINCT			= " DISTINCT ";
	public static String DELETE				= " DELETE ";
	public static String DROP               = " DROP ";
	public static String SEQUENCE           = " SEQUENCE ";
	public static String SEQUENCE_SUFFIX    = "seq";
	public static String SPACE				= " ";
	public static String SELECT				= " SELECT ";
	public static String STAR				= " * ";
	public static String COMMA				= " , ";
	public static String PERIOD			    = ".";
	public static String QUOT				= "'";
	public static String FROM				= " FROM ";
	public static String WHERE				= " WHERE ";
	public static String AND				= " AND ";
	public static String OR					= " OR ";
	public static String LEFT_PARENTHESIS	= " ( ";
	public static String RIGHT_PARENTHESIS	= " ) ";
	public static String MAX				= " MAX ";
	public static String MIN				= " MIN ";
	public static String AVG				= " AVG ";
	public static String ORDER_BY		    = " ORDER BY ";
	public static String GROUP_BY		    = " GROUP BY ";
	public static String ASC		        = " ASC ";
	public static String DESC		        = " DESC ";
	public static String LIMIT		        = " LIMIT ";
	public static String ON				    = " ON ";
    public static String USING			    = " USING ";
	public static String JOIN			    = " JOIN ";
    public static String LEFT_JOIN			= " LEFT JOIN ";
    public static String RIGHT_JOIN			= " RIGHT JOIN ";
    public static String INNER_JOIN			= " INNER JOIN ";
    public static String OUTER_JOIN			= " OUTER JOIN ";
    public static String NATURAL_JOIN		= " NATURAL JOIN ";
    public static String UNION			    = " UNION ";
	public static String AS			        = " AS ";
	public static String NULL			    = " NULL ";
    public static String EXIST              = " EXIST ";
    public static String IN                 = " IN ";
    public static String INTERSECT          = " INTERSECT ";
    public static String NOT                = " NOT ";
    public static String ALTER              = " ALTER ";
    public static String ADD                = " ADD ";
    public static String RENAME             = " RENAME ";
    public static String TABLE              = " TABLE ";
    public static String TO                 = " TO ";
    public static String COLUMN             = " COLUMN ";
    public static String TYPE               = " TYPE ";
    public static String SEMICOLON          = " ; ";
    
    public static String ARBITRARY_SQL_NAME = "COREDB_ARBITRARY_SQL";

	// OPERATION
    public static String E		= " = ";       //EQUAL
    public static String NE		= " != ";      //NOT EQUAL

    public static String L		= " < ";       //NUMERICAL LESS
    public static String LE		= " <= ";      //NUMERICAL LESS or EQUAL
    public static String G		= " > ";       //NUMERICAL GREATER
    public static String GE		= " >= ";      //NUMERICAL GREATER OR EQUAL

	public static String IS		= " IS ";      //IS
    public static String LIKE	= " LIKE ";    //LIKE
    public static String ISNOT	= " IS NOT ";  //IS NO
}
