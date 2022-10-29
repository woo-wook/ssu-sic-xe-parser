import java.util.ArrayList;
import java.util.Arrays;

import exception.SymbolNotFoundException;
import exception.SyntexException;
import util.StringUtil;

/**
 * ����ڰ� �ۼ��� ���α׷� �ڵ带 �ܾ�� ���� �� ��, �ǹ̸� �м��ϰ�, ���� �ڵ�� ��ȯ�ϴ� ������ �Ѱ��ϴ� Ŭ�����̴�. <br>
 * pass2���� object code�� ��ȯ�ϴ� ������ ȥ�� �ذ��� �� ���� symbolTable�� instTable�� ������ �ʿ��ϹǷ� �̸� ��ũ��Ų��.<br>
 * section ���� �ν��Ͻ��� �ϳ��� �Ҵ�ȴ�.
 *
 */
public class TokenTable {
	public static final int MAX_OPERAND = 3;
	
	/* bit ������ �������� ���� ���� */
	public static final int nFlag = 32;
	public static final int iFlag = 16;
	public static final int xFlag = 8;
	public static final int bFlag = 4;
	public static final int pFlag = 2;
	public static final int eFlag = 1;
	
	/* Token�� �ٷ� �� �ʿ��� ���̺���� ��ũ��Ų��. */
	SymbolTable symTab;
	LiteralTable literalTab;
	InstTable instTab;
	ExtTable extTab;
	Section section;
	ModifyTable modifyTab;
	
	/** �� line�� �ǹ̺��� �����ϰ� �м��ϴ� ����. */
	ArrayList<Token> tokenList;
	
	/**
	 * �ʱ�ȭ�ϸ鼭 symTable�� instTable�� ��ũ��Ų��.
	 * @param symTab : �ش� section�� ����Ǿ��ִ� symbol table
	 * @param instTab : instruction ���� ���ǵ� instTable
	 */
	public TokenTable(SymbolTable symTab, InstTable instTab, LiteralTable literalTab, 
			ExtTable extTab, Section section, ModifyTable modifyTab) {
		tokenList = new ArrayList<>(); // ��ū ����Ʈ ����
		
		this.symTab = symTab; // ������ �ɺ����̺� ����
		this.instTab = instTab; // ���� ���̺� ����
		this.literalTab = literalTab; // ���ͷ� ���̺� ����
		this.extTab = extTab; // �ܺ����̺� ����
		this.section = section; // ���� ����
		this.modifyTab = modifyTab; // ���� ���̺� ����
	}
	
	/**
	 * �Ϲ� ���ڿ��� �޾Ƽ� Token������ �и����� tokenList�� �߰��Ѵ�.
	 * @param line : �и����� ���� �Ϲ� ���ڿ�
	 */
	public void putToken(String line) {
		// ��ū ����
		Token token = new Token(line);
		
		// ��ū ����
		token.validation(instTab.findByOperator(token.operator));
		
		// ��ū���̺� �߰�
		tokenList.add(token);
	}
	
	/**
	 * ��ū�� �޾� ��ū ���̺� �߰��Ѵ�.
	 * 
	 * @param token
	 */
	public void setToken(Token token) {
		tokenList.add(token);
	}
	
	
	/**
	 * ������ ��ū�� �����Ѵ�. (CSECT)
	 */
	public void removeLastToken() {
		if(tokenList.size() > 0) { // ��ū ����Ʈ�� ����� �ϳ��� ���� ���
			tokenList.remove(tokenList.size() - 1); // ������ ��ū ����
		}
	}
	
	/**
	 * �ش� ��ū ���̺��� ��ū�� ������� �ݺ��ϸ� �ּҰ��� �Ҵ��Ѵ�.
	 * ����, �ش� �Լ����� nixbpe�� �����ϸ�, �Ϻ� ����� �����ڵ� ���� �м��Ѵ�.
	 */
	public void setLocation() {
		int location = 0;
		Instruction instruction = null;
		
		// ��ū �ݺ�
		for(Token token : tokenList) {
			
			// �ּҰ� �Ҵ� begin --
			instruction = instTab.findByOperator(token.operator);
			
			token.location = location; // ��ū�� �ּҰ� �Ҵ�
			location = location + instruction.format; // �Ϲ� ��ɾ�� ���˸�ŭ ����
			// �ּҰ� �Ҵ� end --
			
			// ����� ���þ� ó�� begin --
			if(StringUtil.nvl(instruction.operator).equals("EXTDEF")) { // �ܺ� ���� ���� �� �ܺ� ���� ���̺� �߰� (Pass1������ �����͸� �߰��ϰ� pass2���� �����Ѵ�)
				this.extTab.addExtdef(token.operand[0]);
			} else if(StringUtil.nvl(instruction.operator).equals("EXTREF")) { // �ܺ� ���� ���� �� �ܺ� ���� ���̺� �߰� (Pass1������ �����͸� �߰��ϰ� pass2���� �����Ѵ�)
				this.extTab.addExtref(token.operand[0]);
			} else if(StringUtil.nvl(instruction.operator).equals("RESW")) { // �ش� ���þ� ���� �� �Ű����� ��ŭ �޸� ���� Ȯ�� (3byte)
				if(StringUtil.isNumber(token.operand[0])) {
					location = location + (Integer.parseInt(token.operand[0]) * 3);
				} else {
					throw new SyntexException("A number must be entered for this parameter.");
				}
			} else if(StringUtil.nvl(instruction.operator).equals("RESB")) { // �ش� ���þ� ���� �� �Ű����� ��ŭ �޸� ���� Ȯ�� (1byte)
				if(StringUtil.isNumber(token.operand[0])) {
					location = location + Integer.parseInt(token.operand[0]);
				} else {
					throw new SyntexException("A number must be entered for this parameter.");
				}
			} else if(StringUtil.nvl(instruction.operator).equals("EQU")) { // �ش� ���þ� ���� �� �޸��� ������ �����Ѵ�.
				if(token.operand[0].equals("*")) { // ���� �޸� �ּҸ� �ּҰ����� ����
					token.location = location;
				} else if(StringUtil.isArithmetic(token.operand[0])) { // ������ ���
					String[] arithmetics = StringUtil.getArithmetic(token.operand[0]);
					char arithmeticSymbol = 0; 
					
					for(String arithmetic : arithmetics) { // �޸� �ּ� ���
						if(!StringUtil.isEmpty(arithmetic)) {
							if(StringUtil.isLetter(arithmetic)) { // ������ ���
								int symbolIndex = this.symTab.search(arithmetic); // �ɺ� ���̺� ��ȸ
								
								if(symbolIndex == -1) { // �ɺ��� ������ ����
									throw new SymbolNotFoundException();
								}
								
								if(arithmeticSymbol == 0) { // ���� ��Ģ����
									token.location = this.symTab.locationList.get(symbolIndex);
								} else if(arithmeticSymbol == '+') {
									token.location = token.location + this.symTab.locationList.get(symbolIndex);
								} else if(arithmeticSymbol == '-') {
									token.location = token.location - this.symTab.locationList.get(symbolIndex);
								} else if(arithmeticSymbol == '*') {
									token.location = token.location * this.symTab.locationList.get(symbolIndex);
								} else if(arithmeticSymbol == '/') {
									token.location = token.location / this.symTab.locationList.get(symbolIndex);
								}
							} else { // ��Ģ���� ��ȣ�� ���
								arithmeticSymbol = arithmetic.charAt(0);
							}
						}
					}
				} else if(StringUtil.isNumber(token.operand[0])) {
					token.location = Integer.parseInt(token.operand[0]);
				}
			} else if(StringUtil.nvl(instruction.operator).equals("LTORG") || StringUtil.nvl(instruction.operator).equals("END")) { // ���ͷ� �Ҵ�
				
				for(int i = 0; i < literalTab.literalList.size(); i++) { // ���� ������ ���ͷ� �ݺ�
					// ���ͷ� ���� ��ȸ
					String literal = literalTab.literalList.get(i); 
					int literalLocation = literalTab.locationList.get(i);
					char literalType = literalTab.literalTypeList.get(i);
					
					if(literalLocation == -1) { // ���ͷ��� �ּҰ� ���� �Ҵ���� �ʾ��� ��
						literalTab.modifyLiteral(literal, location);
						
						location = (int)(location + (literal.length() * (literalType == 'C' ? 1 : 0.5)));
					}
				}
				
			}
			// ����� ���þ� ó�� end --
			
			// �ɺ����̺� ���
			if(token.label != null) { 
				symTab.putSymbol(token.label, token.location);
			}
			
			// ���ͷ� ���̺� ���
			if(token.operand != null) {
				Arrays.stream(token.operand)
					  .filter(x -> StringUtil.isLiteral(x)) // ���ͷ� �˻�
					  .forEach(x -> { // ���ͷ��� �ݺ��Ͽ� ���ͷ� ���̺� ���
						  String literal = StringUtil.getLiteral(x);
						  
						  if(literalTab.search(literal) == -1) { // ���ͷ� ���̺� ��ϵǾ� ���� ���� ��쿡�� ����ϵ��� ó��
							  literalTab.putLiteral(literal, -1, StringUtil.getLiteralType(x));
						  }
					  });
			}
		}
	}
	
	/**
	 * tokenList���� index�� �ش��ϴ� Token�� �����Ѵ�.
	 * @param index
	 * @return : index��ȣ�� �ش��ϴ� �ڵ带 �м��� Token Ŭ����
	 */
	public Token getToken(int index) {
		return tokenList.get(index);
	}
	
	/**
	 * Pass2 �������� ����Ѵ�.
	 * instruction table, symbol table literal table ���� �����Ͽ� objectcode�� �����ϰ�, �̸� �����Ѵ�.
	 * @param index
	 */
	public void makeObjectCode(int index){
		//...
	}
	
	/** 
	 * index��ȣ�� �ش��ϴ� object code�� �����Ѵ�.
	 * @param index
	 * @return : object code
	 */
	public String getObjectCode(int index) {
		return tokenList.get(index).objectCode;
	}
	
	@Override
	public String toString(){
	    return tokenList.toString();
	}
}

/**
 * �� ���κ��� ����� �ڵ带 �ܾ� ������ ������ ��  �ǹ̸� �ؼ��ϴ� ���� ���Ǵ� ������ ������ �����Ѵ�. 
 * �ǹ� �ؼ��� ������ pass2���� object code�� �����Ǿ��� ���� ����Ʈ �ڵ� ���� �����Ѵ�.
 */
class Token{
	//�ǹ� �м� �ܰ迡�� ���Ǵ� ������
	int location;
	String label;
	String operator;
	String[] operand;
	String comment;
	char nixbpe;

	// object code ���� �ܰ迡�� ���Ǵ� ������ 
	String objectCode;
	int byteSize;
	
	/**
	 * Ŭ������ �ʱ�ȭ �ϸ鼭 �ٷ� line�� �ǹ� �м��� �����Ѵ�. 
	 * @param line ��������� ����� ���α׷� �ڵ�
	 */
	public Token(String line) {
		parsing(line);
	}
	
	/**
	 * line�� �������� �м��� �����ϴ� �Լ�. Token�� �� ������ �м��� ����� �����Ѵ�.
	 * @param line ��������� ����� ���α׷� �ڵ�.
	 */
	public void parsing(String line) {
		// �Ľ� �������� ����ϴ� ���� �ʱ�ȭ
		String[] parsingData = line.split("\t");
		
		// label ����
		if(!StringUtil.isEmpty(parsingData[0])) {  // ������ ���
			this.label = parsingData[0]; // label 
		}
		
		// ��ɾ� ����
		if(!StringUtil.isEmpty(parsingData[1])) {
			this.operator = parsingData[1];
		} else {
			throw new SyntexException("Operator is required."); // ��ɾ ������ ����
		}
		
		// operand ����
		if(parsingData.length > 2 
				&& !StringUtil.isEmpty(parsingData[2])) { // ������ ���
			String[] operandData = parsingData[2].split(",");
			
			this.operand = new String[operandData.length]; // ���۷����� ���̸�ŭ �Ҵ�
			this.operand = operandData;
		}
		
		// �ڸ�Ʈ ����
		if(parsingData.length > 3) {
			this.comment = parsingData[3];
		}
		
		//System.out.println(Arrays.toString(parsingData));
	}
	
	/**
	 * ���� ��ɾ ���������� �ԷµǾ����� �����Ѵ�.
	 * 
	 * @param instruction
	 */
	public void validation(Instruction instruction) {
		if(instruction == null) {
			throw new SyntexException("This instruction does not exist."); // ��ɾ �������� ���� ��
		}
		
		if(this.operand != null 
				&& this.operand.length < instruction.minOperandCount) { // �Ű����� ���� �̴� ��
			throw new SyntexException("The minimum number of parameters is "+instruction.minOperandCount+".");
		}
		
		if(instruction.isNewSection()) { // ���ο� ������ �ʿ��� ���
			Assembler.numberOfSection++;
		}
	}
	
	/** 
	 * n,i,x,b,p,e flag�� �����Ѵ�. 
	 * 
	 * ��� �� : setFlag(nFlag, 1); 
	 *   �Ǵ�     setFlag(TokenTable.nFlag, 1);
	 * 
	 * @param flag : ���ϴ� ��Ʈ ��ġ
	 * @param value : ����ְ��� �ϴ� ��. 1�Ǵ� 0���� �����Ѵ�.
	 */
	public void setFlag(int flag, int value) {
		if(this.getFlag(flag) == flag) { // �÷��װ� ���� �Ǿ� ���� ���
			if(value == 0) {
				this.nixbpe -= flag;
			}
		} else { // �÷��װ� ���� �Ǿ� ���� ���� ���
			if(value == 1) {
				this.nixbpe += flag;
			}
		}
	}
	
	/**
	 * ���ϴ� flag���� ���� ���� �� �ִ�. flag�� ������ ���� ���ÿ� �������� �÷��׸� ��� �� ���� �����ϴ� 
	 * 
	 * ��� �� : getFlag(nFlag)
	 *   �Ǵ�     getFlag(nFlag|iFlag)
	 * 
	 * @param flags : ���� Ȯ���ϰ��� �ϴ� ��Ʈ ��ġ
	 * @return : ��Ʈ��ġ�� �� �ִ� ��. �÷��׺��� ���� 32, 16, 8, 4, 2, 1�� ���� ������ ����.
	 */
	public int getFlag(int flags) {
		return nixbpe & flags;
	}
	
	/**
	 * ������ ���Ǽ��� ���� �ӽ÷� �������̵�
	 */
	@Override
	public String toString(){
	    return 	 "{ "
	    		+ "location : " + this.location + ","
	    		+ "label : " + this.label + ", "
	    		+ "operator : " + this.operator + ", "
	    		+ "operand : " + Arrays.toString(this.operand) + ", \n"
				+ "comment : " + this.comment + ", "
				+ "nixbpe : " + (int)this.nixbpe + ", "
				+ "objectCode : " + this.objectCode + ", "
				+ "byteSize : " + this.byteSize
	    		+ " }";
	}
}
