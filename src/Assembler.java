import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import exception.InitException;


/**
 * Assembler : 
 * �� ���α׷��� SIC/XE �ӽ��� ���� Assembler ���α׷��� ���� ��ƾ�̴�.
 * ���α׷��� ���� �۾��� ������ ����. 
 * 1) ó�� �����ϸ� Instruction ���� �о�鿩�� assembler�� �����Ѵ�. 
 * 2) ����ڰ� �ۼ��� input ������ �о���� �� �����Ѵ�. 
 * 3) input ������ ������� �ܾ�� �����ϰ� �ǹ̸� �ľ��ؼ� �����Ѵ�. (pass1) 
 * 4) �м��� ������ �������� ��ǻ�Ͱ� ����� �� �ִ� object code�� �����Ѵ�. (pass2) 
 * 
 * 
 * �ۼ����� ���ǻ��� : 
 *  1) ���ο� Ŭ����, ���ο� ����, ���ο� �Լ� ������ �󸶵��� ����. ��, ������ ������ �Լ����� �����ϰų� ������ ��ü�ϴ� ���� �ȵȴ�.
 *  2) ���������� �ۼ��� �ڵ带 �������� ������ �ʿ信 ���� ����ó��, �������̽� �Ǵ� ��� ��� ���� ����.
 *  3) ��� void Ÿ���� ���ϰ��� ������ �ʿ信 ���� �ٸ� ���� Ÿ������ ���� ����.
 *  4) ����, �Ǵ� �ܼ�â�� �ѱ��� ��½�Ű�� �� ��. (ä������ ����. �ּ��� ���Ե� �ѱ��� ��� ����)
 * 
 *     
 *  + �����ϴ� ���α׷� ������ ��������� �����ϰ� ���� �е��� ������ ��� �޺κп� ÷�� �ٶ��ϴ�. ���뿡 ���� �������� ���� �� �ֽ��ϴ�.
 */
public class Assembler {
	
	// �ش� Ŭ�������� ����ϴ� ���� ���� �ʱ�ȭ
	private static final String INPUT_FILE_PREFIX = "src/";
	private static final String OUTPUT_FILE_PREFIX = "src/output/";
	
	public static int numberOfSection = 0;
	
	/** instruction ���� ������ ���� */
	InstTable instTable;
	/** �о���� input ������ ������ �� �� �� �����ϴ� ����. */
	ArrayList<String> lineList;
	/** ���α׷��� section���� symbol table�� �����ϴ� ����*/
	ArrayList<SymbolTable> symtabList;
	/** ���α׷��� section���� literal table�� �����ϴ� ����*/
	ArrayList<LiteralTable> literaltabList;
	/** ���α׷��� section���� ���α׷��� �����ϴ� ����*/
	ArrayList<TokenTable> TokenList;
	/** ���α׷��� section���� ���ǿ� ������ �����ϴ� ����*/
	ArrayList<ExtTable> extList;
	/** ���α׷��� section ������ �����ϴ� ����*/
	ArrayList<Section> sectionList;
	/** ���α׷��� section ������ �����ϴ� ����*/
	ArrayList<ModifyTable> modifyList;
	
	/** 
	 * Token, �Ǵ� ���þ ���� ������� ������Ʈ �ڵ���� ��� ���·� �����ϴ� ����.   
	 * �ʿ��� ��� String ��� ������ Ŭ������ �����Ͽ� ArrayList�� ��ü�ص� ������.
	 */
	ArrayList<String> codeList;
	
	/**
	 * Ŭ���� �ʱ�ȭ. instruction Table�� �ʱ�ȭ�� ���ÿ� �����Ѵ�.
	 * 
	 * @param instFile : instruction ���� �ۼ��� ���� �̸�. 
	 */
	public Assembler(String instFile) {
		instTable = new InstTable(instFile);
		lineList = new ArrayList<String>();
		symtabList = new ArrayList<SymbolTable>();
		literaltabList = new ArrayList<LiteralTable>();
		TokenList = new ArrayList<TokenTable>();
		codeList = new ArrayList<String>();
		
		// �ű� �߰� ���̺�
		extList = new ArrayList<>();
		sectionList = new ArrayList<>();
		modifyList = new ArrayList<>();
	}

	/** 
	 * ������� ���� ��ƾ
	 */
	public static void main(String[] args) {
		Assembler assembler = new Assembler(INPUT_FILE_PREFIX + "inst.data");
		assembler.loadInputFile(INPUT_FILE_PREFIX + "input.txt");	
		assembler.pass1();

		assembler.printSymbolTable(OUTPUT_FILE_PREFIX + "symtab_20180427");
		assembler.printLiteralTable(OUTPUT_FILE_PREFIX + "literaltab_20180427");
		assembler.pass2();
		assembler.printObjectCode(OUTPUT_FILE_PREFIX + "output_20180427");
	}

	/**
	 * inputFile�� �о�鿩�� lineList�� �����Ѵ�.
	 * @param inputFile : input ���� �̸�.
	 */
	private void loadInputFile(String inputFile) {
		try {
			File file = new File(inputFile);
			
			FileReader fileReader = new FileReader(file); // ���� ��Ʈ�� ����
			BufferedReader bufferedReader = new BufferedReader(fileReader); // ���� ����
			
			String line = ""; // ���� ���� ����
			
			while((line = bufferedReader.readLine()) != null) {
				if(line.trim().length() != 0 
						&& !line.trim().startsWith(".")) { // �� �� ������ ������ ������ ���̰� 0�ϰ��� �ּ��� ��� �߰����� ����
					lineList.add(line);
				}
			}
			
			System.out.println("init line complete!");
			
			bufferedReader.close();
		} catch(FileNotFoundException e) {
			throw new InitException(inputFile + " not found.");
		} catch (IOException e) {
			throw new InitException("An error occurred while reading the " + inputFile + ".");
		}
	}

	/** 
	 * pass1 ������ �����Ѵ�.
	 *   1) ���α׷� �ҽ��� ��ĵ�Ͽ� ��ū������ �и��� �� ��ū���̺� ����
	 *   2) label�� symbolTable�� ����
	 *   
	 *    ���ǻ��� : SymbolTable�� TokenTable�� ���α׷��� section���� �ϳ��� ����Ǿ�� �Ѵ�.
	 */
	private void pass1() {
		// �н� 1 �������� ����ϴ� ���� �ʱ�ȭ
		TokenTable tokenTable = null;
		LiteralTable literalTable = null;
		SymbolTable symbolTable = null;
		ExtTable extTable = null;
		Section section = null;
		ModifyTable modify = null;
		Token newSectionFirstToken = null;
		
		// ��ū ����
		for(String line : lineList) {
			
			// ���� ���� ������ �ʱ�ȭ begin --
			if(TokenList.size() < (Assembler.numberOfSection + 1)) { 
				if(tokenTable != null) { // ��ū ���̺��� ���� �� ���
					newSectionFirstToken = tokenTable.getToken(tokenTable.tokenList.size() - 1); // ������ ��ū ��ȸ (CSECT)
					tokenTable.removeLastToken(); // ������ ��ū ����
					
					tokenTable.setLocation(); // ��ū�� �ּ����� �Ҵ�
				}
				
				symbolTable = new SymbolTable(); // �ű� ������ �ɺ� ���̺� ����
				literalTable = new LiteralTable(); // �ű� ������ ���ͷ� ���̺� ����
				extTable = new ExtTable();  // �ű� ������ �ܺ�����/���� ���̺� ����
				section = new Section(); // ���� ������ �����ϴ� ���� ��ü ����
				modify = new ModifyTable();
				
				// ����Ʈ�� �Ҵ�
				symtabList.add(symbolTable);
				literaltabList.add(literalTable);
				extList.add(extTable);
				sectionList.add(section);
				modifyList.add(modify);
				
				tokenTable = new TokenTable(symbolTable, instTable, literalTable, extTable, section, modify); // ��ū ���̺� ����
				
				if(newSectionFirstToken != null) {
					tokenTable.setToken(newSectionFirstToken); // �ű� ������ ù��°�� ����
					
					newSectionFirstToken = null; // �ٽ� �űԼ����� ���� �� ó���� �� �ֵ��� �ʱ�ȭ
				}
				
				TokenList.add(tokenTable); // ��ū ����Ʈ�� ��ū ���̺� �߰�
			}
			// ���� ���� ������ �ʱ�ȭ end --
			
			tokenTable.putToken(line); // ��ū ����
		}
		
		// ������ ��ū ���̺� �����̼� �Ҵ�
		tokenTable.setLocation();
		
		System.err.println("pass 1 complete!");
	}
	
	/**
	 * �ۼ��� SymbolTable���� ������¿� �°� ����Ѵ�.
	 * @param fileName : ����Ǵ� ���� �̸�
	 */
	private void printSymbolTable(String fileName) {
		try {
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName));
			StringBuilder stringBuilder = new StringBuilder();
			
			for(int i = 0; i < TokenList.size(); i++) { // ���� �� ��ŭ �ݺ�
				TokenTable tokenTable = TokenList.get(i); 
				
				for(int z = 0; z < tokenTable.symTab.symbolList.size(); z++) { // �� ���� �� �ɺ� �� ��ŭ �ݺ�
					stringBuilder.append(tokenTable.symTab.symbolList.get(z)) // ���ۿ� �Է�
							     .append("\t")
							     .append(String.format("%02X", tokenTable.symTab.locationList.get(z)))
								 .append("\n");
				}
				
				stringBuilder.append("\n");
			}
			
			bufferedOutputStream.write(stringBuilder.toString().getBytes()); // ��� 
			bufferedOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("print symbol table complete!");
	}

	/**
	 * �ۼ��� LiteralTable���� ������¿� �°� ����Ѵ�.
	 * @param fileName : ����Ǵ� ���� �̸�
	 */
	private void printLiteralTable(String fileName) {
		try {
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName));
			StringBuilder stringBuilder = new StringBuilder();
			
			for(int i = 0; i < TokenList.size(); i++) { // ���� �� ��ŭ �ݺ�
				TokenTable tokenTable = TokenList.get(i); 
				
				for(int z = 0; z < tokenTable.literalTab.literalList.size(); z++) { // �� ���� �� ���ͷ� �� ��ŭ �ݺ�
					stringBuilder.append(tokenTable.literalTab.literalList.get(z)) // ���ۿ� �Է�
							     .append("\t")
							     .append(String.format("%02X", tokenTable.literalTab.locationList.get(z)))
								 .append("\n");
				}
			}
			
			bufferedOutputStream.write(stringBuilder.toString().getBytes()); // ��� 
			bufferedOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("print literal table complete!");
	}

	/**
	 * pass2 ������ �����Ѵ�.
	 *   1) �м��� ������ �������� object code�� �����Ͽ� codeList�� ����.
	 */
	private void pass2() {
		
		// �н� 1 �������� ����ϴ� ���� �ʱ�ȭ
		TokenTable tokenTable = null;
		SymbolTable symbolTable = null;
		ExtTable extTable = null;
		
		// pass 2 ����
		for(int sectionNumber = 0; sectionNumber < sectionList.size(); sectionNumber++) {
			// ���� �� ���� �ʱ�ȭ 
			tokenTable = TokenList.get(sectionNumber);
			extTable = extList.get(sectionNumber);
			symbolTable = symtabList.get(sectionNumber);
			
			// �ܺ� ���� ���̺��� �����Ѵ�.
			extTable.validation(symbolTable);
			
			// ������Ʈ �ڵ带 �����Ѵ�.
			tokenTable.makeObjectCode();
			
			// ������Ʈ ���α׷��� �����Ѵ�.
			String objectProgram = tokenTable.makeObjectProgram();
			
			codeList.add(objectProgram);
		}
		
		System.err.println("pass 2 complete!");
	}
	
	/**
	 * �ۼ��� codeList�� ������¿� �°� ����Ѵ�.
	 * @param fileName : ����Ǵ� ���� �̸�
	 */
	private void printObjectCode(String fileName) {
		try {
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName));
			StringBuilder stringBuilder = new StringBuilder();
			
			codeList.stream().forEach(stringBuilder::append);
			
			bufferedOutputStream.write(stringBuilder.toString().getBytes()); // ��� 
			bufferedOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("print object program complete!");
	}
	
}
