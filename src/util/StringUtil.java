package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ���ڿ� ��ƿ��Ƽ
 * 
 * @author Cary
 *
 */
public class StringUtil {
	
	private static final String LITERAL_PATTERN_REGEXP = "^={1}[CX]{1}[']{1}[0-9a-zA-Z]*[']{1}$";
	private static final String LITERAL_FIND_PATTERN_REGEXP = "'[0-9a-zA-Z]*'";
	private static int FIND_TOKEN_INDEX = 0;
	private static int FIND_TOKEN_TYPE = -1; // 0 : END, 1 : TOKEN, 2 : OPERATOR

	/**
	 * ���ڿ��� ���̰ų� empty�� ��� �� ���ڿ� ����
	 * 
	 * @param val
	 * @return
	 */
	public static String nvl(String val) {
		val = val.trim();

		return nvl(val, "");
	}

	/**
	 * ���ڿ��� ���̰ų� empty�� ��� rep ����
	 * 
	 * @param val
	 * @param rep
	 * @return
	 */
	public static String nvl(String val, String rep) {
		if (val == null || "".equals(val)) {
			val = rep;
		}
		
		val = val.trim();

		return val;
	}

	/**
	 * ���ڿ��� ���̰ų� empty�� ��� �� ���ڿ� ����
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isEmpty(String val) {
		return nvl(val, "").equals("");
	}

	/**
	 * ���ڿ��� ��� ���ڷθ� �̷���� �ִ��� Ȯ���Ѵ�.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		CharSequence cs = str;

		if (StringUtil.isEmpty(str)) {
			return false;
		}

		final int sz = cs.length();

		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(cs.charAt(i))) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * ���ڿ��� ��� ���ڷθ� �̷���� �ִ��� Ȯ���Ѵ�.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLetter(String str) {
		CharSequence cs = str;

		if (StringUtil.isEmpty(str)) {
			return false;
		}

		final int sz = cs.length();

		for (int i = 0; i < sz; i++) {
			if (!Character.isLetter(cs.charAt(i))) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * ���ڿ��� ���ͷ����� Ȯ���Ѵ�.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLiteral(String str) {
		if(StringUtil.isEmpty(str)) {
			return false;
		}
		
        return Pattern.matches(LITERAL_PATTERN_REGEXP, str);
	}
	
	/**
	 * ���ͷ� ���ڿ����� ���� �κи� �����Ѵ�.
	 * 
	 * @param str
	 * @return
	 */
	public static String getLiteral(String str) {
		if(!StringUtil.isLiteral(str)) {
			return null;
		}
		
		Pattern pattern = Pattern.compile(LITERAL_FIND_PATTERN_REGEXP);
		Matcher matcher = pattern.matcher(str);
		
		if(matcher.find()) {
			String matchers = matcher.group();
			matchers = matchers.replace("'", "");
			
			return matchers;
		}
		
		return null;
	}
	
	/**
	 * ���ͷ��� Ÿ���� �����Ѵ�.
	 * 
	 * @param str
	 * @return
	 */
	public static Character getLiteralType(String str) {
		if(!StringUtil.isLiteral(str)) {
			return null;
		}
		
		return str.charAt(1);
	}
	
	/**
	 * �������� ��ȸ�Ѵ�.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isArithmetic(String str) {
		if(!str.startsWith("+") && !str.startsWith("-") // �������� �����ΰ��� ����
				&& !str.startsWith("*") && !str.startsWith("/")) {
			if(str.contains("-") || str.contains("+")  // ���� ��ȣ�� �ϳ��� �����ϸ�
					|| str.contains("*") || str.contains("/")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ������ ���ڿ��� �迭�� �߶� ��ȸ
	 * 
	 * @param str
	 * @return
	 */
	public static String[] getArithmetic(String str) {
		if(!StringUtil.isArithmetic(str)) {
			return null;
		}
		
		// ������ �ʱ�ȭ
		FIND_TOKEN_INDEX = 0;
		FIND_TOKEN_TYPE = -1;
		int i = 0;
		
		String[] arithmetic = new String[10];
		String data = StringUtil.getToken(str);
		
		while(FIND_TOKEN_TYPE != 0) {
			arithmetic[i++] = data;
			data = StringUtil.getToken(str);
		}
		
		return arithmetic;
	};
	
	
	/**
	 * ���ڿ��� ��ū ������ �ڸ��� �Լ� 
	 * 
	 * @param str
	 * @return
	 */
	private static String getToken(String str) {
		if(FIND_TOKEN_INDEX >= str.length()) { // ���ڿ��� ũ�Ⱑ ���̶��
			FIND_TOKEN_TYPE = 0;
			return null;
		}
		
		if(Character.isLetter(str.charAt(FIND_TOKEN_INDEX))) { // ���ڶ��
			String newStr = "";
			
			for(int i = FIND_TOKEN_INDEX; i < str.length(); i++) { // ���ڰ� ���� �� ���� �ݺ�
				if(Character.isLetter(str.charAt(i))) {
					newStr = newStr + str.charAt(i);
					FIND_TOKEN_INDEX++;
				} else {
					break;
				}
			}
			
			FIND_TOKEN_TYPE = 1; // ��ū
			return newStr;
		} else if(str.charAt(FIND_TOKEN_INDEX) == '+' || str.charAt(FIND_TOKEN_INDEX) == '-' // ���� ��ȣ�� ��� ������ ����
				|| str.charAt(FIND_TOKEN_INDEX) == '*' || str.charAt(FIND_TOKEN_INDEX) == '/') {
			
			char symbol = str.charAt(FIND_TOKEN_INDEX);
			
			FIND_TOKEN_INDEX++;
			FIND_TOKEN_TYPE = 2; // ����
			
			return String.valueOf(symbol);
		}
		
		FIND_TOKEN_TYPE = 0;
		return null;
	}
}
