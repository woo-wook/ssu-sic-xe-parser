import java.util.ArrayList;

import exception.SymbolDuplicationException;
import exception.SymbolNotFoundException;

/**
 * symbol�� ���õ� �����Ϳ� ������ �����Ѵ�.
 * section ���� �ϳ��� �ν��Ͻ��� �Ҵ��Ѵ�.
 */
public class SymbolTable {
	ArrayList<String> symbolList;
	ArrayList<Integer> locationList;
	// ��Ÿ literal, external ���� �� ó������� �����Ѵ�.
	
	/**
	 * ������ ����
	 */
	public SymbolTable() {
		this.symbolList = new ArrayList<>();
		this.locationList = new ArrayList<>();
	}
	
	/**
	 * ���ο� Symbol�� table�� �߰��Ѵ�.
	 * @param symbol : ���� �߰��Ǵ� symbol�� label
	 * @param location : �ش� symbol�� ������ �ּҰ�
	 * ���� : ���� �ߺ��� symbol�� putSymbol�� ���ؼ� �Էµȴٸ� �̴� ���α׷� �ڵ忡 ������ ������ ��Ÿ����. 
	 * ��Ī�Ǵ� �ּҰ��� ������ modifySymbol()�� ���ؼ� �̷������ �Ѵ�.
	 */
	public void putSymbol(String symbol, int location) {
		int symbolIndex = this.search(symbol); // �ɺ� �˻�
		
		// �ɺ��� ���� �� ���
		if(symbolIndex > -1) {
			throw new SymbolDuplicationException(); // �ɺ� �ߺ� exception �߻�
		}
		
		// �ɺ����̺� �߰�
		this.symbolList.add(symbol);
		this.locationList.add(location);
	}
	
	/**
	 * ������ �����ϴ� symbol ���� ���ؼ� ����Ű�� �ּҰ��� �����Ѵ�.
	 * @param symbol : ������ ���ϴ� symbol�� label
	 * @param newLocation : ���� �ٲٰ��� �ϴ� �ּҰ�
	 */
	public void modifySymbol(String symbol, int newLocation) {
		int symbolIndex = this.search(symbol); // �ɺ� �˻�
		
		// �ɺ��� �������� ���� ���
		if(symbolIndex == -1) {
			throw new SymbolNotFoundException(); // �ɺ� ���� exception �߻�
		}

		// �ɺ����̺� ������Ʈ
		this.locationList.set(symbolIndex, newLocation);
	}
	
	/**
	 * ���ڷ� ���޵� symbol�� � �ּҸ� ��Ī�ϴ��� �˷��ش�. 
	 * @param symbol : �˻��� ���ϴ� symbol�� label
	 * @return symbol�� ������ �ִ� �ּҰ�. �ش� symbol�� ���� ��� -1 ����
	 */
	public int search(String symbol) {
		int address = 0;
		
		for(String sym : symbolList) { // �ɺ��� �ݺ��Ͽ�
			if(sym.equals(symbol)) {
				return address; // �ش� �ɺ��� ã�� �ɺ��� ��� ����
			}
			
			address++;
		}
		
		return -1;
	}
}
