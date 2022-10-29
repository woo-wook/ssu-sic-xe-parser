package exception;

/**
 * ��ū �Ľ� ���� ���� �� ������ �߻��� ȣ��
 * 
 * @author Cary
 *
 */
public class SyntexException extends RuntimeException {

	private static final long serialVersionUID = 1L;


	public SyntexException() {
		super("A syntex error occurred.");
	}
	
	
	public SyntexException(String msg) {
		super(msg);
	}
}
