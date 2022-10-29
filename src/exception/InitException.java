package exception;

/**
 * Init ���� ���� �� ������ �߻��� ȣ��
 * 
 * @author Cary
 *
 */
public class InitException extends RuntimeException {

	private static final long serialVersionUID = 1L;


	public InitException() {
		super("An error occurred during initialization.");
	}
	
	
	public InitException(String msg) {
		super(msg);
	}
}
