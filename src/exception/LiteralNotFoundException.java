package exception;

/**
 * ���ͷ��� ���� �� ȣ��
 * 
 * @author Cary
 *
 */
public class LiteralNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LiteralNotFoundException() {
		super("Litera not found.");
	}
}
