package exception;

/**
 * ���ͷ��� �ߺ� ��ϵǾ��� �� ȣ��
 * 
 * @author Cary
 *
 */
public class LiteralDuplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LiteralDuplicationException() {
		super("Duplicate literal exist.");
	}
}
