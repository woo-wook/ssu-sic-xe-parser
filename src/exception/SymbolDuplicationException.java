package exception;

/**
 * �ɺ��� �ߺ� ��ϵǾ��� �� ȣ��
 * 
 * @author Cary
 *
 */
public class SymbolDuplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SymbolDuplicationException() {
		super("Duplicate symbol exist.");
	}
}
