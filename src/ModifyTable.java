import java.util.ArrayList;

/**
 * ������ �ʿ��� ������ ��Ƶδ� ���̺�
 * 
 * @author Cary
 *
 */
public class ModifyTable {
	ArrayList<Modify> modifyList;
	
	public ModifyTable() {
		modifyList = new ArrayList<>();
	}
	
	/**
	 * ������ �ʿ��� ������ �����Ѵ�.
	 * 
	 * @param location
	 * @param length
	 * @param sign
	 * @param label
	 */
	public void add(int location, int length, char sign, String label) {
		modifyList.add(new Modify(location, length, sign, label));
	}
}

/**
 * ���� ���� ��ü
 * 
 * @author Cary
 *
 */
class Modify {
	public int location; // �ּ� ��
	public int lenght; // ���� : �Ϻ�
	public char sign; // ��ȣ
	public String label; // ���̺�
	
	private static StringBuilder stringBuilder = new StringBuilder();
	
	public Modify(int location, int length, char sign, String label) {
		this.location = location;
		this.lenght = length;
		this.sign = sign;
		this.label = label;
	}
	
	/**
	 * ���� ������ ������Ʈ ���α׷� ���Ŀ� ���߾� ����Ѵ�.
	 * 
	 * @return
	 */
	public String print() {
		stringBuilder.setLength(0);
		
		stringBuilder.append("M")
					 .append(String.format("%06X", location))
				     .append(String.format("%02X", lenght))
					 .append(sign)
					 .append(label)
					 .append("\n");
	
		return stringBuilder.toString();
	}
}
