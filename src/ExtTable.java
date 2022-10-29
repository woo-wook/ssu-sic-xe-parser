import java.util.ArrayList;
import java.util.Arrays;

/**
 * �ܺ� ���ǿ� �ܺ� ������ �����ϴ� Ŭ����
 * 
 * @author Cary
 *
 */
public class ExtTable {
	ArrayList<Extdef> extdef; // �ܺ� ����
	ArrayList<Extref> extref; // �ܺ� ����
	
	public ExtTable() {
		this.extdef = new ArrayList<>();
		this.extref = new ArrayList<>();
	}
	
	/**
	 * �ܺ� ���� �߰�
	 * 
	 * @param str
	 */
	public void addExtdef(String str) { 
		String[] data = str.split(",");
		
		Arrays.stream(data).forEach(x-> extdef.add(new Extdef(x)));
	}
	
	/**
	 * �ܺ� ���� �߰�
	 * 
	 * @param str
	 */
	public void addExtref(String str) {
		String[] data = str.split(",");
		
		Arrays.stream(data).forEach(x-> extref.add(new Extref(x)));
	}
}


/**
 * �ܺ� ���� ��ü
 * 
 * @author Cary
 *
 */
class Extdef {
	public String name;
	public int location;
	
	public Extdef(String name) {
		this.name = name;
		this.location = -1;
	}
	
	public void modifyExtdef(int location) {
		this.location = location;
	}
}

/**
 * �ܺ� ���� ��ü
 * 
 * @author Cary
 *
 */
class Extref {
	public String name;
	
	public Extref(String name) {
		this.name = name;
	}
}