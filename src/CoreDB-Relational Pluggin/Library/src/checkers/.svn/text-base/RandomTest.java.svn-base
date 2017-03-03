package checkers;

import java.sql.Time;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import coredb.controller.IEntityControllerBase;
import coredb.database.JDBCTypeConverter;
import coredb.unit.AttributeClass;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.unit.IndexClass;

/**
 * 
 * @author Stephen
 */
public class RandomTest {

	/**
	 * DATA_NUM defines that the number of data will be inserted into the table. The number should be no more than 10000; otherwise, it will consumes a huge resource
	 */
	private final static int DATA_NUM = 1000;
	
	/**
	 *  TABLE_NUM define that the number of table  will be generated in the database 
	 */
	private final static int TABLE_NUM = 3;
	
    /**
	 * TABLE_NAME_MIN_LENGTH = 7 , TABLE_NAME_MAX_LENGTH = 9
	 * These setting will allow generate three different lengths of 
	 * table name e.g. [abcdefg],[abcdefgh],[abcdefghi]
     */
	private final static int TABLE_NAME_MIN_LENGTH = 7;
	private final static int TABLE_NAME_MAX_LENGTH = 8;

    /**
     * COLUMN_NUM = 10
     * The number of column in a table
     * [A|B|C|D|.......|10]
     */
	private final static int COLUMN_NUM = 10;

    /**
	 * COLUMN_NAME_MIN_LENTH= 7 , COLUMN_NAME_MAX_LENTH = 9
	 * These setting will allow generate three different lengths of 
	 * column name e.g. [a234567],[a2345678],[a23456789]
     */
	private final static int COLUMN_NAME_MIN_LENGTH = 10;
	private final static int COLUMN_NAME_MAX_LENGTH = 12;
	
	/**
	 * PRIMARYKEY_NUM, INDEX_NUM and AUTOINCREMENT_NUM define
     * the number of primary key,
     * index and
     * auto increment element in the table
	 */
	private final static int PRIMARYKEY_NUM = 8;
	private final static int INDEX_NUM = 5;
	private final static int AUTOINCREMENT_NUM = 3;
	
	/**
	 * ROOT is the seed of the random mechanism to create the table schemas. Different of value from this variable will customize 
	 * different test cases.
	 */
	private final static int ROOT = 6;
	
	/**
	 * DIFF is the controller to guarantee the similarity of column name in the database. If we
	 * demand the unique column name, set it greater than the COLUMN_NUM
	 */
	private final static int DIFF = 101;
	
	/**
	 * ELEMENTS are the basic factors to generate the random string.
	 */
	private final static char[]  ELEMENTS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
										'a', 'b','c', 'd','e', 'f','g', 'h','i', 'j','k', 'l', 'm', 'n','o', 'p','q', 'r', 's','t', 'u', 'v', 'w', 'x', 'y', 'z' };
	
	/**
	 * CLASSPOOL is the collection of Java type which is supported by the coredb API.
	 */
	@SuppressWarnings("unchecked")
	private final static Class[] CLASSPOOL = { Integer.class, Character.class, Double.class,String.class,Date.class,Time.class,Timestamp.class,Boolean.class};
	
	private static HashMap<String, String> colMap;
	private static HashMap<String, String> doubleMap;
	private static HashMap<Integer, Integer> integerMap;
	private static long now = System.currentTimeMillis();
	
	/**
	 * Generate a list of entityinstance by random
	 * @param ecl the list entitysecurity class
	 * @param iec the interface of entity controller
	 * @return a list of entityinstance
	 */
	@SuppressWarnings("finally")
	public static List<EntityInstance> generateEntityInstances ( List<EntitySecurityClass> ecl, IEntityControllerBase iec ) {
		List<EntityInstance> beans = new LinkedList<EntityInstance>(); 
		doubleMap = new HashMap<String, String>();
		integerMap = new HashMap<Integer, Integer>();
		//System.err.println(" -------------------------------- Generate Data By Random =Start --------------------------------");
		try {
			for( EntityClass ec : ecl){
				for(int i = 0; i< DATA_NUM ;i++ ){
					EntityInstance bean = iec.createEntityInstanceFor(ec.getName());
					for(AttributeClass ac : ec.getAttributeClassList()){
						if(!ac.isAutoIncrement()){
							bean = generateRandomData(bean, ac.getName(), ac.getSqlType());
						}
					}
					beans.add(bean);
					//Thread.sleep(1);
				}		
				doubleMap.clear();
				integerMap.clear();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			//System.err.println(" -------------------------------- Generate Data By Random = END --------------------------------\n");
			return beans;
		}
	}
	
	private static Integer getRandomInteger(Integer i){
		Integer value = integerMap.get(i);
		if(value!=null){
			Random random = new Random(System.currentTimeMillis());
			i = getRandomInteger(random.nextInt());
		}else{
			integerMap.put(i,i);
		}
		return i;
	}
	
	private static Double getRandomDouble(Double d){
		String value = doubleMap.get(String.valueOf((d*100000)));
		if(value!=null){
			Random random = new Random(System.currentTimeMillis());
			d = getRandomDouble(random.nextDouble());
		}else{
			doubleMap.put(String.valueOf((d*100000)),String.valueOf((d*100000)));
		}
		return d;
	}
	
	/**
	 * Generate the test data by random
	 * @param bean a entity instance
	 * @param name the attribute name as which value will be inserted
	 * @param sqlType the JDBC type
	 * @return a entityinstance
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private static EntityInstance generateRandomData( EntityInstance bean, String name, String sqlType) throws InterruptedException{
		Class classType = JDBCTypeConverter.toJAVAType(sqlType);
		Random random = new Random(System.currentTimeMillis());
		if(classType.equals(Integer.class)) 	{ bean.set(name, getRandomInteger(random.nextInt())); return bean; }
		if(classType.equals(String.class)) 		{ bean.set(name, generateNames(System.currentTimeMillis(), 20, 15)); return bean; }
		if(classType.equals(Double.class)) 		{ bean.set(name, getRandomDouble(random.nextDouble())); return bean; }
		if(classType.equals(Boolean.class)) 	{ bean.set(name, (System.currentTimeMillis()%2 == 0 ? true : false) ); return bean; }
		if(classType.equals(Date.class)) 		{ bean.set(name, new Date(now += 3600*1000*24)); return bean; }
		if(classType.equals(Time.class)) 		{ bean.set(name, new Time(now += 1000)); return bean; }
		if(classType.equals(Timestamp.class)) 	{ bean.set(name, new Timestamp(now += 3600*1000*24)); return bean; }
		if(classType.equals(Character.class)) 	{ bean.set(name, 'A'); return bean; }
		return bean;
	}
	/**
	 * Generate the table schema by random
	 * @param iec the interface of entitycontroller
	 * @return a list of entitysecurity class
	 */
	@SuppressWarnings("unchecked")
	public static List<EntitySecurityClass> generateSchemasByRandom(IEntityControllerBase iec ){
		try {
			List<EntitySecurityClass> ecl = new LinkedList<EntitySecurityClass>();
			int columndiff			= DIFF;
			int ticker 				= 1;
			colMap = new HashMap<String, String>();
			//System.err.println(" -------------------------------- Generate Scheam By Random = Start --------------------------------");
			for(int i = ROOT ; i < TABLE_NUM + ROOT; i++){
				int indexNum 			= INDEX_NUM;
				int primaryKeyNum 		= PRIMARYKEY_NUM;
				int autoIncrementNum	= AUTOINCREMENT_NUM;
				
				// To make sure the table name is unique
				String tableName = generateNames(i, TABLE_NAME_MAX_LENGTH, TABLE_NAME_MIN_LENGTH);
				//System.out.println("\n---------- TableName : "+tableName +" -----------\n");
				List<AttributeClass> acl = new LinkedList<AttributeClass>();
				List<IndexClass> indexClasses = new LinkedList<IndexClass>();
				
				for( int j = (ROOT + TABLE_NUM)*(ROOT + TABLE_NUM) + columndiff*ticker; j < COLUMN_NUM + (ROOT + TABLE_NUM)*(ROOT + TABLE_NUM) + columndiff*ticker; j++){	
					boolean isIndex = false;
					String colName 	= generateNames( j, COLUMN_NAME_MAX_LENGTH, COLUMN_NAME_MIN_LENGTH );
					Class type 	= generateClassType( j );			
					if(autoIncrementNum > 0){
						type = Integer.class;
					}
					if( indexNum > 0 && !type.equals(Boolean.class)&& !type.equals(Character.class))  { isIndex = true; indexNum--; type = Integer.class;}
					//System.out.println("---------- Column : " + type.getSimpleName() + "$" + colName + " -----------");
					AttributeClass ac = new AttributeClass(type.getSimpleName() + "$" + colName, type);
					if(isIndex){IndexClass indexClass = new IndexClass(ac, null, false); indexClasses.add(indexClass);}
					if( primaryKeyNum > 0 && !type.equals(Boolean.class)&& !type.equals(Character.class)) { 
						ac.setPrimaryKey(true); 
						primaryKeyNum--; 
						if( autoIncrementNum >0){
							autoIncrementNum --;
							ac.setAutoIncrement(true);
						}
					}
					acl.add(ac);
				}
				ecl.add(new EntitySecurityClass(tableName, acl.toArray(new AttributeClass[0]), indexClasses.toArray(new IndexClass[0])));
				ticker++;
			}	
			//System.err.println("\n -------------------------------- Generate Scheam By Random = End --------------------------------\n");
			return ecl;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 *  Skip the repeated name and generate another one
	 * @param name the string type 
	 * @param seed the seed for random function
	 * @param max the max length of the name
	 * @param min the min length of the name
	 * @return the unique name
	 * @throws InterruptedException
	 */
	private static String skipRepeatName(String name, long seed, int max, int min) throws InterruptedException{	
		String value = colMap.get(name.toLowerCase());	
		if(value != null){
			seed = seed + 100;
			name = generateNames(seed, max, min);	
		}else{
			colMap.put(name.toLowerCase(), name);	
		}
		return name;
	}
	
	/**
	 * Generate the Java class type by random
	 * @param seed the seed for random function
	 * @return Java calss type
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private static Class generateClassType( long seed ) throws InterruptedException{
		Random random  = new Random(seed);
		int i = Math.abs(random.nextInt()) % CLASSPOOL.length;
		return CLASSPOOL[i];
	}
	
	/**
	 * Generate the name (String type) by random
	 * @param seed the seed for random function
	 * @param max the max length of the name
	 * @param min the min length of the name
	 * @return the unique name
	 * @throws InterruptedException
	 */
	private static String generateNames(long seed, int max, int min) throws InterruptedException{
		Random random  = new Random(seed);
		String result = "";
		boolean isFirstChar = true;
		int Length = ((max - min) == 0) ? min : (Math.abs(random.nextInt())%(max - min) + min);
		for (int i = 0; i < Length; i++){
			int elementId = Math.abs(random.nextInt()) % ELEMENTS.length;
			if(isFirstChar)  {
				while(elementId < 10){ 
					elementId = Math.abs(random.nextInt()) % ELEMENTS.length; 				
				}
				isFirstChar = false;
			}		
			result += ELEMENTS[elementId];
		}
		result = skipRepeatName(result, seed, max, min);
		return result;
	}
}
