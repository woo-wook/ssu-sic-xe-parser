import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import exception.InitException;


/**
 * ��� instruction�� ������ �����ϴ� Ŭ����. instruction data���� �����Ѵ�
 * ���� instruction ���� ����, ���� ��� ����� �����ϴ� �Լ�, ���� ������ �����ϴ� �Լ� ���� ���� �Ѵ�.
 */
public class InstTable {
	/** 
	 * inst.data ������ �ҷ��� �����ϴ� ����.
	 *  ��ɾ��� �̸��� ��������� �ش��ϴ� Instruction�� �������� ������ �� �ִ�.
	 */
	HashMap<String, Instruction> instMap;
	
	/**
	 * Ŭ���� �ʱ�ȭ. �Ľ��� ���ÿ� ó���Ѵ�.
	 * @param instFile : instuction�� ���� ���� ����� ���� �̸�
	 */
	public InstTable(String instFile) {
		instMap = new HashMap<String, Instruction>();
		openFile(instFile);
	}
	
	/**
	 * �Է¹��� �̸��� ������ ���� �ش� ������ �Ľ��Ͽ� instMap�� �����Ѵ�.
	 */
	public void openFile(String fileName) {
		try {
			File file = new File(fileName);
			
			FileReader fileReader = new FileReader(file); // ���� ��Ʈ�� ����
			BufferedReader bufferedReader = new BufferedReader(fileReader); // ���� ����
			
			String line = ""; // ���� ���� ����
			
			while((line = bufferedReader.readLine()) != null) {
				Instruction instruction = new Instruction(line);
				
				instMap.put(instruction.operator, instruction);
				
				//System.out.println("instruction : " + instruction);
			}
			
			System.out.println("init instruction complete!");
			
			bufferedReader.close();
		} catch(FileNotFoundException e) {
			throw new InitException(fileName + " not found.");
		} catch (IOException e) {
			throw new InitException("An error occurred while reading the " + fileName + ".");
		}
	}
	
	/**
	 * ���̺� ����Ǿ� �ִ� �ν�Ʈ������ ��ɾ�� �˻�
	 * 
	 * @param operator
	 * @return
	 */
	public Instruction findByOperator(String operator) {
		return instMap.get(operator);
	}
}
/**
 * ��ɾ� �ϳ��ϳ��� ��ü���� ������ InstructionŬ������ ����.
 * instruction�� ���õ� �������� �����ϰ� �������� ������ �����Ѵ�.
 */
class Instruction {
	
	// ��ɾ�
	String operator;
	
	// ���� �ڵ�
	int opcode;
	
	// ����
	int format;
	
	// �ּ� ���۷����� ��
	int minOperandCount;
	
	/**
	 * Ŭ������ �����ϸ鼭 �Ϲݹ��ڿ��� ��� ������ �°� �Ľ��Ѵ�.
	 * @param line : instruction �����Ϸκ��� ���پ� ������ ���ڿ�
	 */
	public Instruction(String line) {
		parsing(line);
	}
	
	/**
	 * �Ϲ� ���ڿ��� �Ľ��Ͽ� instruction ������ �ľ��ϰ� �����Ѵ�.
	 * @param line : instruction �����Ϸκ��� ���پ� ������ ���ڿ�
	 */
	public void parsing(String line) {
		String[] parsingData = line.split("\\|"); // | ������ ����
		
		// ��ɾ�
		this.operator = parsingData[0]; 
		
		// ����
		this.format = Integer.parseInt(parsingData[1]); 
		
		if(parsingData[2].equals("NULL")) {
			parsingData[2] = "-1";
		}
		
		// ���� �ڵ�
		this.opcode = Integer.parseInt(parsingData[2], 16);
		
		
		// �ּ� ���۷����� ��
		this.minOperandCount = Integer.parseInt(parsingData[3]); 
	}
	
	/**
	 * �ش� ��ɾ ���ο� ������ ������� Ȯ���Ѵ�.
	 * @return
	 */
	public boolean isNewSection() {
		return this.operator.equals("CSECT");
	}
	
	/**
	 * ������ ���Ǽ��� ���� �ӽ÷� �������̵�
	 */
	@Override
	public String toString(){
	    return 	 "{ "
	    		+ "operator : " + this.operator + ", "
	    		+ "format : " + this.format + ", "
	    		+ "opcode : " + this.opcode + ", "
	    		+ "minOperandCount : " + this.minOperandCount
	    		+ " }";
	}
}
