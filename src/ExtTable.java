import java.util.ArrayList;
import java.util.Arrays;

import exception.SymbolNotFoundException;

/**
 * �ܺ� ���ǿ� �ܺ� ������ �����ϴ� Ŭ����
 * 
 * @author Cary
 *
 */
public class ExtTable {
	ArrayList<Extdef> extdef; // �ܺ� ����
	ArrayList<Extref> extref; // �ܺ� ����
	
	private static StringBuilder stringBuilder = new StringBuilder();
	
	public ExtTable() {
		this.extdef = new ArrayList<>();
		this.extref = new ArrayList<>();
	}
	
	/**
	 * �ܺ� ���� �߰�
	 * 
	 * @param str
	 */
	public void addExtdef(String[] data) { 
		Arrays.stream(data).forEach(x-> extdef.add(new Extdef(x)));
	}
	
	/**
	 * �ܺ� ���� �߰�
	 * 
	 * @param str
	 */
	public void addExtref(String[] data) {
		Arrays.stream(data).forEach(x-> extref.add(new Extref(x)));
	}
	
	/**
	 * �ܺ� ���� ������ �����Ѵ�.
	 * 
	 * @param symbolTab
	 */
	public void validation(SymbolTable symbolTab) {
		extdef.stream().forEach(x-> {
			int symbolIndex = symbolTab.search(x.name);
			
			if(symbolIndex == -1) {
				throw new SymbolNotFoundException();
			}
			
			x.modifyExtdef(symbolTab.locationList.get(symbolIndex));
		});
	}

	/**
	 * �ܺ� ���� ������ �����Ͱ� �ִ��� Ȯ���Ѵ�.
	 * 
	 * @param str
	 * @return
	 */
	public boolean isExtref(String str) {
		return extref.stream()
				     .anyMatch(x-> x.name.equals(str));
	}
	
	/**
	 * �ܺ� ���� ������ ������Ʈ ���α׷� ���Ŀ� ���߾� ����Ѵ�.
	 * 
	 * @return
	 */
	public String printDef() {
		stringBuilder.setLength(0);
		
		stringBuilder.append("D");
		this.extdef.forEach(x-> stringBuilder.append(x.print()));
		stringBuilder.append("\n");
		
		return stringBuilder.toString();
	}
	
	
	/**
	 * �ܺ� ���� ������ ������Ʈ ���α׷� ���Ŀ� ���߾� ����Ѵ�.
	 * 
	 * @return
	 */
	public String printRef() {
		stringBuilder.setLength(0);
		
		stringBuilder.append("R");
		this.extref.forEach(x-> stringBuilder.append(String.format("%-6s", x.name)));
		stringBuilder.append("\n");
		
		return stringBuilder.toString();
	}
}


/**
 * �ܺ� ���� ��ü
 * 
 * @author Cary
 *
 */
class Extdef {
	// �̸�
	public String name;
	// �ּ�
	public int location;
	
	private static StringBuilder stringBuilder = new StringBuilder();
	
	public Extdef(String name) {
		this.name = name;
		this.location = -1;
	}
	
	/**
	 * �ܺ� ���� ��ü�� �ּҸ� �����Ѵ�.
	 * 
	 * @param location
	 */
	public void modifyExtdef(int location) {
		this.location = location;
	}
	
	/**
	 * �ܺ� ���� ��ü�� ������Ʈ ���α׷� ���Ŀ� ���߾� ����Ѵ�.
	 * 
	 * @return
	 */
	public String print() {
		stringBuilder.setLength(0);
		
		stringBuilder.append(String.format("%-6s", name))
				     .append(String.format("%06X", location));
	
		return stringBuilder.toString();
	}
}

/**
 * �ܺ� ���� ��ü
 * 
 * @author Cary
 *
 */
class Extref {
	// �̸�
	public String name;
	
	private static StringBuilder stringBuilder = new StringBuilder();
	
	public Extref(String name) {
		this.name = name;
	}
	
	/**
	 * �ܺ� ���� ��ü�� ������Ʈ ���α׷� ���Ŀ� ���߾� ����Ѵ�.
	 * 
	 * @return
	 */
	public String print() {
		stringBuilder.setLength(0);
		
		stringBuilder.append(String.format("%-6s", name));
	
		return stringBuilder.toString();
	}
}