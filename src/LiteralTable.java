import java.util.ArrayList;

import exception.LiteralDuplicationException;
import exception.LiteralNotFoundException;

/**
 * literal�� ���õ� �����Ϳ� ������ �����Ѵ�.
 * section ���� �ϳ��� �ν��Ͻ��� �Ҵ��Ѵ�.
 */
public class LiteralTable {
	ArrayList<String> literalList;
	ArrayList<Integer> locationList;
	ArrayList<Character> literalTypeList;
	// ��Ÿ literal, external ���� �� ó������� �����Ѵ�.
	
	/**
	 * ������ ����
	 */
	public LiteralTable() {
		this.literalList = new ArrayList<>();
		this.locationList = new ArrayList<>();
		this.literalTypeList = new ArrayList<>();
	}
	
	/**
	 * ���ο� Literal�� table�� �߰��Ѵ�.
	 * @param literal : ���� �߰��Ǵ� literal�� label
	 * @param location : �ش� literal�� ������ �ּҰ�
	 * @param literalType : �ش� literal�� Ÿ��
	 * ���� : ���� �ߺ��� literal�� putLiteral�� ���ؼ� �Էµȴٸ� �̴� ���α׷� �ڵ忡 ������ ������ ��Ÿ����. 
	 * ��Ī�Ǵ� �ּҰ��� ������ modifyLiteral()�� ���ؼ� �̷������ �Ѵ�.
	 */
	public void putLiteral(String literal, int location, Character literalType) {
		int literalIndex = this.search(literal);
		
		if(literalIndex > -1) {
			throw new LiteralDuplicationException();
		}
		
		this.literalList.add(literal);
		this.locationList.add(location);
		this.literalTypeList.add(literalType);
	}
	
	/**
	 * ������ �����ϴ� literal ���� ���ؼ� ����Ű�� �ּҰ��� �����Ѵ�.
	 * @param literal : ������ ���ϴ� literal�� label
	 * @param newLocation : ���� �ٲٰ��� �ϴ� �ּҰ�
	 */
	public void modifyLiteral(String literal, int newLocation) {
		int literalIndex = this.search(literal);
		
		if(literalIndex == -1) {
			throw new LiteralNotFoundException();
		}
		
		this.locationList.set(literalIndex, newLocation);
	}
	
	/**
	 * ���ڷ� ���޵� literal�� � �ּҸ� ��Ī�ϴ��� �˷��ش�. 
	 * @param literal : �˻��� ���ϴ� literal�� label
	 * @return literal�� ������ �ִ� �ּҰ�. �ش� literal�� ���� ��� -1 ����
	 */
	public int search(String literal) {
		int address = 0;
		
		for(String lit : literalList) { // ���ͷ��� �ݺ��Ͽ�
			if(lit.equals(literal)) {
				return address; // �ش� ���ͷ��� ã�� ���ͷ��� ��� ����
			}
			
			address++;
		}
		
		return -1;
	}
}
