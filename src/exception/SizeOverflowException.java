package exception;

/**
 * �Ҵ��� ũ�⺸�� ū �����Ͱ� �߻����� ��
 * 
 * @author Cary
 *
 */
public class SizeOverflowException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SizeOverflowException() {
		super("Data overflow.");
	}
}
