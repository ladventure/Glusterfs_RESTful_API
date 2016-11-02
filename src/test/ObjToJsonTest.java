
/**
 * 文件名   :   ObjToJsonTest.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月21日
 * 修改内容：      <修改内容>
 */
package test;

import java.util.ArrayList;  
import java.util.Collection;  
import java.util.Iterator;  
import java.util.List;

import com.platform.entities.PostData;

import net.sf.json.JSONArray;  
import net.sf.json.JSONObject; 

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年10月21日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class ObjToJsonTest {
	 int id;
	/**
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @see [类、类#方法、类#成员]
	 */
	public ObjToJsonTest(int id ) {
		// TODO Auto-generated constructor stub
		super();
		this.id=id;
	}
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	public static void main(String[] args) {  
//        TestJsonBean();  
//        TestJsonAttribute();  
        TestJsonArray();          
    }  
  
    @SuppressWarnings("rawtypes")  
    private static void TestJsonArray() {  
        Student student1 = new Student(1);  
//        student1.setId(1);  
//        student1.setName("jag");  
//        student1.setSex("man");  
//        student1.setAge(25);  
//      
//          
//        Student student2 = new Student();  
//        student2.setId(2);  
//        student2.setName("tom");  
//        student2.setSex("woman");  
//        student2.setAge(23);  
//      
//      
        ObjToJsonTest objToJsonTest=new ObjToJsonTest(1); 
       
        List<String> list = new ArrayList<String>();  
        list.add("fasdf");  
        list.add("aaaa");  
        PostData postData=new PostData(list);
        JSONObject  jsonArray = JSONObject.fromObject(postData);  
        System.out.println(jsonArray.toString());  
          
//        JSONArray new_jsonArray=JSONArray.fromObject(jsonArray.toArray());  
//        Collection java_collection=JSONArray.toCollection(new_jsonArray);  
//        if(java_collection!=null && !java_collection.isEmpty())  
//        {  
//            Iterator it=java_collection.iterator();  
//            while(it.hasNext())  
//            {  
//                JSONObject jsonObj=JSONObject.fromObject(it.next());  
//                Student stu=(Student) JSONObject.toBean(jsonObj,Student.class);  
//                System.out.println(stu.getName());  
//            }  
//        }  
    }  
  
    
}  
class Student{	
	private int id;
//	private String name;
//	private String sex;
//	private int age;
//	/**
//	 * <一句话功能简述>
//	 * <功能详细描述>
//	 * @see [类、类#方法、类#成员]
//	 */
//	public Student() {
//		// TODO Auto-generated constructor stub
//		
//	}
	public Student(int id) {
		// TODO Auto-generated constructor stub
		super();
		this.id=id;
	}
//	/**
//	 * @return the id
//	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
//	/**
//	 * @return the name
//	 */
//	public String getName() {
//		return name;
//	}
//	/**
//	 * @param name the name to set
//	 */
//	public void setName(String name) {
//		this.name = name;
//	}
//	/**
//	 * @return the sex
//	 */
//	public String getSex() {
//		return sex;
//	}
//	/**
//	 * @param sex the sex to set
//	 */
//	public void setSex(String sex) {
//		this.sex = sex;
//	}
//	/**
//	 * @return the age
//	 */
//	public int getAge() {
//		return age;
//	}
//	
//	/**
//	 * @param age the age to set
//	 */
//	public void setAge(int age) {
//		this.age = age;
//	}
//	
}