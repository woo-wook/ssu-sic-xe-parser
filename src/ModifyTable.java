
/**
 * ������ �ʿ��� ������ ��Ƶδ� ���̺�
 * 
 * @author Cary
 *
 */
public class ModifyTable {
	
}

class Modify {
	public static StringBuilder stringBuilder = new StringBuilder(); 
	
	public int location; // �ּ� ��
	public int lenght; // ���� : �Ϻ�
	public char sign; // ��ȣ
	public String label;
	
	public Modify(int location, int length, char sign, String label) {
		this.location = location;
		this.lenght = length;
		this.sign = sign;
		this.label = label;
	}
	
	public String print() {
		stringBuilder.setLength(0);
		
		stringBuilder.append(String.format("%06X", location))
				    .append(String.format("%02X", lenght))
					.append(sign)
					.append(label);
	
		return stringBuilder.toString();
	}
}
