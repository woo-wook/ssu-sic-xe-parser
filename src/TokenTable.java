import java.util.ArrayList;
import java.util.Arrays;

import exception.LiteralNotFoundException;
import exception.SizeOverflowException;
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
	 * ����, �Ϻ� ����� �����ڵ� ���� �м��Ѵ�.
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
			if(StringUtil.nvl(instruction.operator).equals("START") 
					|| StringUtil.nvl(instruction.operator).equals("CSECT")) { // �ű� ���� ���� ��
				if(!StringUtil.isEmpty(token.label)) { // ���̺��� �ݵ�� �����ؾ� ��
					this.section.programName = token.label;
					
					if(StringUtil.nvl(instruction.operator).equals("START")) { // START ��쿡�� ���� �ּ� �Ҵ� 
						this.section.isMain = true; // START�� ���� ����
						if(StringUtil.isNumber(token.operand[0])) { // ���� �ּҴ� ����
							this.section.startAddress = Integer.parseInt(token.operand[0]);
						} else {
							throw new SyntexException("A number must be entered for this parameter."); // �Ķ���ʹ� �ݵ�� ���ڿ��� ��
						}
					}
				} else { 
					throw new SyntexException("START instruction label cannot be null."); // ��ŸƮ ����� �ݵ�� ���̺��� �����ؾ� ��
				}
				
			} else if(StringUtil.nvl(instruction.operator).equals("EXTDEF")) { // �ܺ� ���� ���� �� �ܺ� ���� ���̺� �߰� (Pass1������ �����͸� �߰��ϰ� pass2���� �����Ѵ�)
				this.extTab.addExtdef(token.operand);
			} else if(StringUtil.nvl(instruction.operator).equals("EXTREF")) { // �ܺ� ���� ���� �� �ܺ� ���� ���̺� �߰� (Pass1������ �����͸� �߰��ϰ� pass2���� �����Ѵ�)
				this.extTab.addExtref(token.operand);
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
		
		section.programLength = location;
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
	public void makeObjectCode(){
		// pass2 ���� ����ϴ� ���� �ʱ�ȭ
		Instruction instruction = null;
		StringBuilder binaryObjectCode = new StringBuilder();
		int addressingMode = 0;
		
		// ��ū �ݺ�
		for(Token token : tokenList) {
			// ��� ���� ��ȸ
			instruction = instTab.findByOperator(token.operator);
			
			// ���� �ʱ�ȭ
			binaryObjectCode.setLength(0); 
					
			// nixbpe ���� begin --
			if(token.operand != null) {
				addressingMode = StringUtil.getAddressingMode(token.operand[0]);
				token.setFlag(addressingMode, 1); // ��巹�� ��� ����
				
				if(token.operand.length > 1 
						&& StringUtil.nvl(token.operand[1]).equals("X")) {
					token.setFlag(xFlag, 1);
				}
				
				if(instruction.format == 3 // ���� 3 �����̸鼭
						&& (addressingMode == nFlag || addressingMode == (nFlag+iFlag))) { // ��巹�� ��尡 ���� �����ų�, SIC/XE��� �� ���
					token.setFlag(pFlag, 1);
				}
				
				if(instruction.format == 4) { // ���� 4 ������ ���
					token.setFlag(eFlag, 1);
				}
			}
			// nixbpe ���� end --
			
			// object code ���� begin --
			if(instruction.opcode != -1) { // ������� �����ڰ� �ƴ� ��츸 ó��, ������� �����ڴ� �ڿ��� ó����� ��.
				String opcodeBinary = Integer.toBinaryString(0x100 | instruction.opcode).substring(1);
				
				if(instruction.format == 1) { // ���� 1�� ���
					binaryObjectCode.append(opcodeBinary); // ���� 1�� opcode�� 8��Ʈ ��� ����Ѵ�.
				} else if(instruction.format == 2) { // ���� 2�� ���
					binaryObjectCode.append(opcodeBinary); // ���� 2�� opcode�� 8��Ʈ ��� ����Ѵ�.
					
					int registerNo = StringUtil.getRegisterNumber(token.operand[0]); // ���� 2�� ù �Ķ���ʹ� ������ �������� ��ȣ
					
					String binaryRegisterNo = Integer.toBinaryString(0x10 | registerNo).substring(1); // �������� ��ȣ�� ���̳ʸ��� ����
					binaryObjectCode.append(binaryRegisterNo); // �ڵ忡 �߰�
					
					if(instruction.operator.equals("SHIFTR") 
							|| instruction.operator.equals("SHIFTL")) { // ���� 2 ��ɾ��� ���� ��ɾ�� 2��° ���۷��忡 ���ڰ� ��� ����.
						binaryRegisterNo = Integer.toBinaryString(0x10 | Integer.parseInt(token.operand[1])).substring(1);
						binaryObjectCode.append(binaryRegisterNo);
					} else {
						if(token.operand.length > 1 && !StringUtil.isEmpty(token.operand[1])) {
							registerNo = StringUtil.getRegisterNumber(token.operand[1]); // ���� 2�� �ι�° �Ķ���ʹ� �������Ͱų� ����
							
							binaryRegisterNo = Integer.toBinaryString(0x10 | registerNo).substring(1); // �������� ��ȣ�� ���̳ʸ��� ����
							binaryObjectCode.append(binaryRegisterNo);
						} else {
							binaryRegisterNo = Integer.toBinaryString(0x10 | 0).substring(1); // �������� ��ȣ�� ���̳ʸ��� ����
							binaryObjectCode.append(binaryRegisterNo);
						}
					}
				} else if(instruction.format == 3 || instruction.format == 4) { // ���� 3Ȥ�� 4�� ���
					
					if(instruction.operator.equals("RSUB")) {
						binaryObjectCode.setLength(0);
						binaryObjectCode.append(Integer.toBinaryString(0x4F0000));
					} else {
						opcodeBinary = opcodeBinary.substring(0, 6); // 6��Ʈ�� �ڸ���
						binaryObjectCode.append(opcodeBinary); // ���� 3, 4�� opcode�� 6��Ʈ ����Ѵ�.
						
						String nixbpeBinary = Integer.toBinaryString(0b1000000 | token.nixbpe).substring(1); // nixbpe�� ���̳ʸ� ���·� ����
						binaryObjectCode.append(nixbpeBinary); // ���� 3, 4�� nixbpe�� ���
						
						int disp = 0;
						String operand = token.operand[0];
						
						if(addressingMode != (nFlag+iFlag)) { // ���������ų� ���������� ���
							operand = operand.substring(1);
						} else if(StringUtil.isLiteral(operand)) {
							String literal = StringUtil.getLiteral(operand);
							
							int literalIndex = literalTab.search(literal);
							
							if(literalIndex > -1) {
								disp = literalTab.locationList.get(literalIndex) - (token.location + instruction.format); // target - PC;
							} else {
								throw new LiteralNotFoundException(); // ���ͷ��� ���� ��
							}
						}
						
						// �ּ� ó��
						if(disp == 0) { // ���ͷ��� �̹� �ּҰ��� �������Ƿ� �н�
							if(StringUtil.isLetter(operand)) { // �ɺ��� ���
								int symbolIndex = symTab.search(operand);
								
								if(symbolIndex > -1) {
									disp = symTab.locationList.get(symbolIndex);
									
									if(instruction.format == 3) {
										disp = disp - (token.location + instruction.format);
									}
								} else {
									if(extTab.isExtref(operand)) {
										disp = 0;
										
										modifyTab.add(token.location+1, 5, '+', operand);
									} else {
										throw new SymbolNotFoundException();
									}
								}
							} else if(StringUtil.isNumber(operand)) { // ������ ���
								disp = Integer.parseInt(operand);
							}
						}
						
						String targetBinary = "";
						
						if(instruction.format == 3) {
							targetBinary = Integer.toBinaryString(0x1000 | disp).substring(1);
							targetBinary = targetBinary.substring(targetBinary.length()-12, targetBinary.length());
						} else if(instruction.format == 4) {
							targetBinary = Integer.toBinaryString(0x100000 | disp).substring(1);
							targetBinary = targetBinary.substring(targetBinary.length()-20, targetBinary.length());
						}
						
						binaryObjectCode.append(targetBinary); // ���� Object Code
					}
				}
				
				int objectCode = Integer.parseInt(binaryObjectCode.toString(), 2);
				
				String hexObjectCode = String.format("%0"+(instruction.format * 2)+"X", objectCode);
				
				token.objectCode = hexObjectCode;
				token.byteSize = token.objectCode.length() / 2;
			// object code ���� end --
			} else if(instruction.operator.equals("BYTE") || instruction.operator.equals("WORD")) { // �ش� ������ ������Ʈ �ڵ带 �Ҵ��ؾ���
				String operand = token.operand[0];
				
				if(StringUtil.isForm(operand)) { // ������ �ִ� �Ű������� ��� (EX: X'05')
					String hexData = StringUtil.getFormDataToHex(operand); // �����͸� HEX �������� ����
					
					if((hexData.length() / 2) <= instruction.format) { // �ش� �����Ͱ� �����÷ο����� Ȯ��
						token.objectCode = hexData; // ������ �Ҵ�
						token.byteSize = token.objectCode.length() / 2;
					} else {
						throw new SizeOverflowException(); 
					}
				} else if(StringUtil.isArithmetic(operand)) { // ������ ���
					String[] arithmetics = StringUtil.getArithmetic(operand); // ���� ���� ��ȸ
					char arithmeticSymbol = 0; // ���� �ɺ�
					
					for(String arithmetic : arithmetics) { // ���� �ݺ�
						if(!StringUtil.isEmpty(arithmetic)) {
							if(StringUtil.isLetter(arithmetic)) { // �ɺ��� ���
								int symbolIndex = this.symTab.search(arithmetic); // �ɺ� ���̺� ��ȸ
								
								if(symbolIndex == -1) { 
									if(extTab.isExtref(arithmetic)) { // �ɺ��� �ƴ����� �ܺ� ������ ���
										arithmeticSymbol = arithmeticSymbol == 0 ? '+' : arithmeticSymbol; // ���� �����ʹ� +�� 
										
										modifyTab.add(token.location, instruction.format*2, arithmeticSymbol, arithmetic); // ���� ���̺� ���
									} else {
										throw new SymbolNotFoundException(); // �ɺ��� ���� ���
									}
								} 
							} else { // ��Ģ���� ��ȣ�� ���
								arithmeticSymbol = arithmetic.charAt(0);
							}
						}
					}
					
					if(instruction.format == 3) { // word
						token.objectCode = Integer.toHexString(0x1000000 | 0).substring(1); // 6�ڸ� �Ҵ�
					} else { // byte
						token.objectCode = Integer.toHexString(0x100 | 0).substring(1); // 2�ڸ� �Ҵ�
					}
					
					token.byteSize = token.objectCode.length() / 2; 
				} else if(StringUtil.isLetter(operand)) { // ������ ���
					int symbolIndex = this.symTab.search(operand); // �ɺ� ���̺� ��ȸ
					
					if(symbolIndex == -1) { 
						if(extTab.isExtref(operand)) { // �ɺ��� �ƴ����� �ܺ� ������ ���
							modifyTab.add(token.location, instruction.format*2, '+', operand); // ���� ���̺� ���
						} else {
							throw new SymbolNotFoundException();
						}
					} 
					
					if(instruction.format == 3) { // word
						token.objectCode = Integer.toHexString(0x1000000 | 0).substring(1); // 6�ڸ� �Ҵ�
					} else { // byte
						token.objectCode = Integer.toHexString(0x100 | 0).substring(1); // 2�ڸ� �Ҵ�
					}
					
					token.byteSize = token.objectCode.length() / 2;
				} else if(StringUtil.isNumber(operand)) { // ������ ���
					if(instruction.format == 3) { // word
						token.objectCode = Integer.toHexString(0x1000000 | Integer.parseInt(operand)).substring(1); // 6�ڸ� �Ҵ�
					} else { // byte
						token.objectCode = Integer.toHexString(0x100 | Integer.parseInt(operand)).substring(1); // 2�ڸ� �Ҵ�
					}
					
					token.byteSize = token.objectCode.length() / 2;
				}
			}
		}
	}
	
	/**
	 * Pass2 �������� ����Ѵ�.
	 * Object Program�� �����Ͽ� ���ڿ��� ���·� �����Ѵ�.
	 * @param index
	 */
	public String makeObjectProgram() {
		StringBuilder stringBuilder = new StringBuilder();
		
		// ������Ʈ ���α׷��� ��� ���� ����
		stringBuilder.append(String.format("H%-6s%06X%06X\n", section.programName, section.startAddress, section.programLength));
		
		// ������Ʈ ���α׷��� �ܺ� ���� ���� ����
		if(extTab.extdef.size() > 0) {
			stringBuilder.append(extTab.printDef());
		}
		
		// ������Ʈ ���α׷��� �ܺ� ���� ���� ����
		if(extTab.extref.size() > 0) {
			stringBuilder.append(extTab.printRef());
		}
		
		// ������Ʈ ���α׷��� �ٵ� ���� ����
		StringBuilder bodyBuilder = new StringBuilder();
		int startLocation = section.startAddress; // ���� �ּ�
		boolean isNewLine = false; // ���ο� �� ���� ����
		
		// ��ū �ݺ�
		for(Token token : tokenList) {
			
			// ���ڿ��� ���̰� �ʰ��Ͽ��ų�, ���ο� ���� ���� �÷��װ� ���ϰ��
			if((bodyBuilder.length() + token.byteSize > 60) || isNewLine) { 
				if(!StringUtil.isEmpty(bodyBuilder.toString())) { // ������ ������� ���� ���
					// ���� ���
					stringBuilder.append(String.format("T%06X%02X%s\n", startLocation, bodyBuilder.length() / 2, bodyBuilder.toString()));
					
					// ���� �ʱ�ȭ
					isNewLine = false; 
					bodyBuilder.setLength(0); 
				}
			}
			
			if(!StringUtil.isEmpty(token.objectCode)) { // ������Ʈ �ڵ尡 �ִ� ���
				if(StringUtil.isEmpty(bodyBuilder.toString())) { // ���ڿ��� ��� �ִ� ���
					startLocation = token.location; // ���� �ּҸ� �ش� �ڵ�� ����
				}
				
				bodyBuilder.append(token.objectCode); // �ش� �ڵ� �߰�
			} else if(token.operator.equals("RESB") || token.operator.equals("RESW")) { // ���� ����� ���� ���
				if(!StringUtil.isEmpty(bodyBuilder.toString())) { // ������ �ű� �������� ����
					isNewLine = true;
				}
			} else if(token.operator.equals("LTORG") || token.operator.equals("END")) { // ���ͷ� ����� �ʿ��� ���
				if(literalTab.literalList.size() > 0) { // ���ͷ��� ���� �� ��쿡��
					if(StringUtil.isEmpty(bodyBuilder.toString())) {
						startLocation = literalTab.locationList.get(0); // ���ͷ��� �ּҸ� ���� �ּҷ� ����
					}
					
					bodyBuilder.append(literalTab.print()); // ���ͷ� ���
				}
			}
		}
		
		if(!StringUtil.isEmpty(bodyBuilder.toString())) { // ���� ���� ���
			stringBuilder.append(String.format("T%06X%02X%s\n", startLocation, bodyBuilder.length() / 2, bodyBuilder.toString()));
		}
		
		// ������Ʈ ���α׷��� ���� ���� ���
		if(modifyTab.modifyList.size() > 0) {
			modifyTab.modifyList.forEach(x-> stringBuilder.append(x.print()));
		}
		
		
		// ����
		stringBuilder.append("E");
		if(section.isMain) {
			stringBuilder.append(String.format("%06X", section.startAddress));	
		}
		stringBuilder.append("\n\n");
		
		return stringBuilder.toString();
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
	    		+ "operand : " + Arrays.toString(this.operand) + ","
				+ "nixbpe : " + (int)this.nixbpe
	    		+ " }";
	}
}
