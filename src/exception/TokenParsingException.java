package exception;

/**
 * ��ū �Ľ� ���� ���� �� ������ �߻��� ȣ��
 * 
 * @author Cary
 *
 */
public class TokenParsingException extends RuntimeException {

	private static final long serialVersionUID = 1L;


	public TokenParsingException() {
		super("An error occurred during the token parsing.");
	}
	
	
	public TokenParsingException(String msg) {
		super(msg);
	}
}
