package com.sunwayworld.escm.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link String}��صĹ�����
 */
public final class StringUtils {
	
	/**
     * �ж�{@link CharSequence} �Ƿ�Ϊ{@code null} �� {@code ""}
     * 
     * @param cs  {@link CharSequence} �����ж�
     * @return {@code true} ���{@link CharSequence} {@code null} �� {@code ""}
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    /**
     * �ж�{@link CharSequence} �Ƿ�Ϊ{@code null} ��ȫΪ���ַ�
     * 
     * @param cs  {@link CharSequence} �����ж�
     * @return {@code true} ���{@link CharSequence} {@code null} ��ȫΪ���ַ�
     */
    public static boolean isBlank(final CharSequence cs) {
    	int pos = 0;
    	
    	if (cs == null || (pos = cs.length()) == 0) {
    		return true;
    	}
    	
    	while(pos>0) {
    		if (Character.isWhitespace(cs.charAt(pos---1)) == false) {
                return false;
            }
    	}
    	
    	return true;
    }
    
    /**
     * �������޶���һ��������������Ӵ�{@link String}������:
     * StringUtils.substringsBetween("[a][b][c]", "[", "]") ���᷵�غ��� "a""b","c"��{@link String} {@link List}
     *
     * @param target  ������ȡ��Ŀ��{@link String}
     * @param open  �Ӵ���ʼ�ı��
     * @param closeTag  �Ӵ��رյı��
     * @return {@link String} {@link List} 
     */
    public static List<String> substringsBetween(final String target, final String openTag, final String closeTag) {
    	final List<String> subStrings = new ArrayList<String>();
    	
        if (isBlank(target)|| isBlank(openTag) || isBlank(closeTag)) {
            return subStrings;
        }
        final int strLen = target.length();
        
        if (strLen == 0) {
            return subStrings;
        }
        
        final int closeLen = closeTag.length();
        final int openLen = openTag.length();
        
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = target.indexOf(openTag, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            final int end = target.indexOf(closeTag, start);
            if (end < 0) {
                break;
            }
            subStrings.add(target.substring(start, end));
            pos = end + closeLen;
        }
        
        return subStrings;
    }
    
    /**
	 * Ҫ�ѵ�һ���ַ���ɴ�д
	 * 
	 * @param target ��ת����{@link String}
	 * @return ��һ���ַ���ת���ɴ�д
	 */
    public static String capitalize(final String target) {
    	if (StringUtils.isBlank(target))
    		return target;

        final char firstChar = target.charAt(0);
        
        if (Character.isUpperCase(firstChar)) {
            return target;
        }

        return Character.toUpperCase(firstChar) + target.substring(1);
    }
    
    /**
	 * Ҫ�� ��һ���ַ����Сд
	 * 
	 * @param target ��ת����{@link String}
	 * @return ��һ���ַ���ת���ɴ�д
	 */
    public static String uncapitalize(final String target) {
    	if (StringUtils.isBlank(target))
    		return target;

        final char firstChar = target.charAt(0);
        
        if (Character.isLowerCase(firstChar)) {
            return target;
        }

        return Character.toLowerCase(firstChar) + target.substring(1);
    }
	
	/**
	 * �滻�ַ���Ĵ�ָ����λ�ÿ�ʼ�����������ֶΣ��滻�������ǿ���ָ����
	 * 
	 * @param target Ŀ��{@link String}���������滻
	 * @param searchString Ҫ����ѯ��{@link String}
	 * @param replacement �滻��{@link String}
	 * @param fromIndex �����λ�ÿ�ʼ���� 0��ʼ����
	 * @param max �ܹ�Ҫ�滻�Ĵ�����{@code -1}���滻����
	 * @return �滻����ֶ�
	 */
	public static final String replace(final String target, final String searchString, 
			final String replacement, final int fromIndex, final int max) {
		if (isEmpty(target) || isEmpty(searchString) || max==0)
			return target;
		
		int start = fromIndex;
		
		if (fromIndex<0)
			start = 0;
		
		int end = target.indexOf(searchString, start);
		
		if (end == -1)
			return target;
		
		final StringBuffer sb = new StringBuffer(target.substring(0, start));
		
		final int searchStringLen = searchString.length();
		
		int count = 0;
		
		while(end!=-1) {
			sb.append(target.substring(start, end)).append(replacement);
			
			start = end + searchStringLen;
			
			if (++count == max) // �ѵ��滻����
				break;
					
			end = target.indexOf(searchString, start);
		}
		
		sb.append(target.substring(start));
		
		return sb.toString();
	}
	
	/**
	 * �滻�ַ���Ĵ�ָ����λ�ÿ�ʼ���������ĵ�һ���ֶΣ�
	 * 
	 * @param target Ŀ��{@link String}���������滻
	 * @param searchString Ҫ����ѯ��{@link String}
	 * @param replacement �滻��{@link String}
	 * @param fromIndex �����λ�ÿ�ʼ���� 0��ʼ����
	 * @return �滻����ֶ�
	 */
	public static final String replaceOnce(final String target, final String searchString, 
			final String replacement, final int fromIndex) {
		return replace(target, searchString, replacement, fromIndex, 1);
	}
	
	/**
	 * �滻�ַ���Ĵ�ָ����λ�ÿ�ʼ���������������ֶΣ�
	 * 
	 * @param target Ŀ��{@link String}���������滻
	 * @param searchString Ҫ����ѯ��{@link String}
	 * @param replacement �滻��{@link String}
	 * @param fromIndex �����λ�ÿ�ʼ���� 0��ʼ����
	 * @return �滻����ֶ�
	 */
	public static final String replaceAll(final String target, final String searchString, 
			final String replacement, final int fromIndex) {
		return replace(target, searchString, replacement, fromIndex, -1);
	}
	
	/**
	 * �滻���޶���һ��������������Ӵ�{@link String}����ǣ���{@code Map}��Key��Ӧ��ֵ�滻
	 * 
	 * @param target Ŀ��{@link String}���������滻
	 * @param openTag �Ӵ���ʼ�ı��
	 * @param closeTag �Ӵ��رյı��
	 * @param map ����Ҫ�滻Value��{@code Map}������KeyΪ��д
	 * @return �滻����ֶ�
	 */
	public static final String replaceBetween(final String target, final String openTag,
			final String closeTag,final Map<String, Object> map) {		
		final List<String> substrings = substringsBetween(target, openTag, closeTag);
		
		if (substrings.isEmpty()) {
			return target;
		} else {
			String replaceTarget = target;
			
			for (String substring : substrings) {
				final String replacement = ConvertUtils.convert(map.get(substring), String.class);
				
				replaceTarget = replaceAll(replaceTarget, openTag + substring + closeTag, replacement, 0);
			}
			
			return replaceTarget;
		}
	}
	
	/**
	 * �滻���޶���{@code span}��ǩ����Ӵ�{@link String}������ǣ���{@code Map}�е�Value�滻<br>
	 * {@code span}��{@code id}Ϊ{@code Map}�е�Key��Key��Ӧ��ֵ���滻��ǩ����Ӵ�{@link String}<br>
	 * ��ʽΪ��&lt;span id='key'&gt;xxxxx&lt;/span&gt;
	 * 
	 * @param target Ŀ��{@link String}���������滻
	 * @param map ����Ҫ�滻Value��{@code Map}
	 * @return �滻����ֶ�
	 */
	public static final String replaceBetweenSpan(final String target, final Map<String, Object> map) {
		if (map.isEmpty()) {
			return target;
		}
		
		String replaceTarget = target;
		
		final Iterator<Entry<String, Object>> iterator =  map.entrySet().iterator();
		
		while (iterator.hasNext()) {
			final Entry<String, Object> entry = iterator.next();
			
			final String regex = "<span\\s+id\\s*=\\s*('|\")" + entry.getKey() + "('|\")\\s*>(.|\\s)*?</span>"; // ƥ���������ʽ
			
			final Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); // �����ִ�Сд
			
			final Matcher m = p.matcher(replaceTarget);
			
			replaceTarget = m.replaceAll("<span id='" + entry.getKey() + "'>" + ObjectUtils.toString(entry.getValue()) + "</span>");
		}
		
		return replaceTarget;
	}
	
	/**
	 * ɾ����ʼ��һ����{@link String}������еĻ�
	 * 
	 * @param target Ŀ��{@link String}
	 * @param remove Ҫɾ����{@link String}
	 * @return ɾ�����{@link String}
	 */
	public static final String removeStart(final String target, final String remove) {
		if (isEmpty(target) || isEmpty(remove)) {
			return target;
		}

		if (target.startsWith(remove)){
			return target.substring(remove.length());
		}
	        
		return target;
	}
	
	/**
	 * ɾ����β��һ����{@link String}������еĻ�
	 * 
	 * @param target Ŀ��{@link String}
	 * @param remove Ҫɾ����{@link String}
	 * @return ɾ�����{@link String}
	 */
	public static final String removeEnd(final String target, final String remove) {
		if (isEmpty(target) || isEmpty(remove)) {
			return target;
		}

		if (target.endsWith(remove)){
			 return target.substring(0, target.length() - remove.length());
		}
	        
		return target;
	}
	
	/**
     * �ж�{@link CharSequence} ��ǰ׺�Ƿ���{@link CharSequence}�������ǲ����ִ�С��
	 * 
	 * @param target Ŀ��{@link CharSequence} 
	 * @param prefix ǰ׺{@link CharSequence}
	 * @return true �����Ŀ���ǰ׺
	 */
	public static boolean startsWithIgnoreCase(final CharSequence target, final CharSequence prefix) {
		if (target == null || prefix == null) {
            return target == null && prefix == null;
        }
		
		if (prefix.length() == 0)
        	return true;
		
        if (prefix.length() > target.length()) {
            return false;
        }
        
        int pos = 0;
        
        while(pos<prefix.length()) {
        	if (Character.toLowerCase(target.charAt(pos)) != Character.toLowerCase(prefix.charAt(pos)))
        		return false;
        	pos++;
        }
        
        return true;
    }
	
	/**
     * �ж�{@link CharSequence} �ĺ�׺�Ƿ���{@link CharSequence}�����ִ�С��
     * 
	 * @param target Ŀ��{@link CharSequence} 
	 * @param suffix ǰ׺{@link CharSequence}
	 * @return true �����Ŀ���ǰ׺
	 */
	public static boolean endsWith(final CharSequence target, final CharSequence suffix) {
		if (target == null || suffix == null) {
            return target == null && suffix == null;
        }
		
		if (suffix.length() == 0)
        	return true;
        
		if (suffix.length() > target.length()) {
            return false;
        }
        
        final int offset = target.length() - suffix.length();
        int pos = 0;
        
        while(pos<suffix.length()) {        	
        	if (target.charAt(offset + pos) != suffix.charAt(pos))
        		return false;
        	
        	pos++;
        }
        
        return true;
	}
	
	/**
     * �ж�{@link CharSequence} �ĺ�׺�Ƿ���{@link CharSequence}�������ǲ����ִ�С��
     * 
	 * @param target Ŀ��{@link CharSequence} 
	 * @param suffix ǰ׺{@link CharSequence}
	 * @return true �����Ŀ���ǰ׺
	 */
	public static boolean endsWithIgnoreCase(final CharSequence target, final CharSequence suffix) {
		if (target == null || suffix == null) {
            return target == null && suffix == null;
        }
		
		if (suffix.length() == 0)
        	return true;
        
		if (suffix.length() > target.length()) {
            return false;
        }
        
        final int offset = target.length() - suffix.length();
        int pos = 0;
        
        while(pos<suffix.length()) {        	
        	if (Character.toLowerCase(target.charAt(offset + pos)) != Character.toLowerCase(suffix.charAt(pos)))
        		return false;
        	
        	pos++;
        }
        
        return true;
	}

	/**
	 * �ظ��ַ���������һ���µ��ַ���
	 * 
	 * @param str Ҫ�ظ����ַ���
	 * @param repeat �ظ��Ĵ���
	 * @return �ظ����µ��ַ���
	 */
	public static String repeat(final String str, final int repeat) {
		if (str == null)
			return null;
		
		if (repeat <= 0)
			return "";
		
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < repeat; i++) {
			sb.append(str);
		}
		
		return sb.toString();
	}
	
	/**
	 * ��չ{@link String#trim()}������ɾ����ͷ�ͽ�β�Ŀո񡢻س���ˮƽ�Ʊ�������еȶ�Ҫȥ��
	 * 
	 * @param target Ŀ��{@link CharSequence}
	 * @return �滻���µ��ַ���
	 */
	public static final String trim(final String target) {
		if (target == null)
			return null;
		
		char[] chars = target.toCharArray();
		
		int start = 0;
		int end = chars.length;
		
		for (char c : chars) {
			if (isBlankChar(c)) {
				start++;
			} else {
				break;
			}
		}
		
		for (int i = chars.length-1; i > 0; i--) {
			if (isBlankChar(chars[i])) {
				end--;
			} else {
				break;
			}
		}
		
		return String.copyValueOf(chars, start, end-start);
	}
	
	/**
	 * ����������ʽ��{@link String#split()}����
	 * 
	 * @param target Ŀ��{@link String}
	 * @param separator �ָ���
	 * @return ��ֳɵ��ַ�������
	 */
	public static final String[] split(final String target, final String separator) {
		if (isBlank(target)) {
			return new String[0];
		}
		
		if (isEmpty(separator)) {
			return new String[] {target};
		}
		
		int length = separator.length();
		
		final List<String> splitStrings = new ArrayList<String>();
		
		int lastMatchPos = 0;
		int pos = 0;
		
		while((pos = target.indexOf(separator, pos)) != -1) {
			if (lastMatchPos == 0) {
				splitStrings.add(target.substring(0, pos));
			} else {
				splitStrings.add(target.substring(lastMatchPos + length, pos));
			}
			
			lastMatchPos = pos++;
		}
		
		if (lastMatchPos == 0) {
			return new String[] {target};
		}
		
		splitStrings.add(target.substring(lastMatchPos + length));
		
		return splitStrings.toArray(new String[splitStrings.size()]);
	}
	
	/**
	 * ����������ʽ�Ļ�ȡ�ַ�����ƥ�������ķ���
	 * 
	 * @param target Ŀ��{@link String}
	 * @param match Ҫƥ����ַ���
	 * @return ƥ�������
	 */
	public static final int matchCount(final String target, final String match) {
		if (isBlank(target)
				|| isEmpty(match)) {
			return 0;
		}
		
		int count = 0;
		int pos = 0;
		
		while((pos = target.indexOf(match, pos + 1)) != -1) {
			count++;
		}
		
		return count;
	}
	
	/**
	 * �ж�{@link CharSequence} �Ƿ�Ϊ{@code null} ��ȫΪ�����ַ�
	 * @param cs cs  {@link CharSequence} �����ж�
	 * @return �����{@code null} ���߷������ַ� ���� false�� ����������ַ����� true
	 */
	public static final Boolean isDigit(final CharSequence cs) {
		int pos = 0;
    	
    	if (cs == null || (pos = cs.length()) == 0) {
    		return false;
    	}
    	
    	while(pos>0) {
    		if (Character.isDigit(cs.charAt(pos---1)) == false) {
                return false;
            }
    	}
		return true;
	}
	
	/*********************************************************************
	 * ˽�з���
	 *********************************************************************/
	/**
	 * �жϸ�����{@link char}�Ƿ��ǿ�
	 * 
	 * @param c ���ڱȽϵ�{@link char}
	 * @return true ����ǿգ�����false
	 */
	private static final boolean isBlankChar(char c) {
		return (c == ' ' || c == '\t' || c == '\b' || c == '\r' || c == '\n' || c == '\f');
	}
}
