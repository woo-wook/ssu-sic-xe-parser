package exception;

/**
 * �ɺ��� ���� �� ȣ��
 * 
 * @author Cary
 *
 */
public class SymbolNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SymbolNotFoundException() {
		super("Symbol not found.");
	}
}
