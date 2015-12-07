package com.sunwayworld.escm.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link String}相关的工具类
 */
public final class StringUtils {
	
	/**
     * 判断{@link CharSequence} 是否为{@code null} 或 {@code ""}
     * 
     * @param cs  {@link CharSequence} 用于判断
     * @return {@code true} 如果{@link CharSequence} {@code null} 或 {@code ""}
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    /**
     * 判断{@link CharSequence} 是否为{@code null} 或全为空字符
     * 
     * @param cs  {@link CharSequence} 用于判断
     * @return {@code true} 如果{@link CharSequence} {@code null} 或全为空字符
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
     * 搜索被限定在一组标记里面的所有子串{@link String}，例如:
     * StringUtils.substringsBetween("[a][b][c]", "[", "]") 将会返回含有 "a""b","c"的{@link String} {@link List}
     *
     * @param target  用于提取的目标{@link String}
     * @param open  子串开始的标记
     * @param closeTag  子串关闭的标记
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
	 * 要把第一个字符变成大写
	 * 
	 * @param target 被转换的{@link String}
	 * @return 第一个字符被转换成大写
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
	 * 要把 第一个字符变成小写
	 * 
	 * @param target 被转换的{@link String}
	 * @return 第一个字符被转换成大写
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
	 * 替换字符里的从指定的位置开始搜索出来的字段，替换的数量是可以指定的
	 * 
	 * @param target 目标{@link String}被搜索和替换
	 * @param searchString 要被查询的{@link String}
	 * @param replacement 替换的{@link String}
	 * @param fromIndex 从这个位置开始算起 0开始计数
	 * @param max 总共要替换的次数，{@code -1}是替换所有
	 * @return 替换完的字段
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
			
			if (++count == max) // 已到替换次数
				break;
					
			end = target.indexOf(searchString, start);
		}
		
		sb.append(target.substring(start));
		
		return sb.toString();
	}
	
	/**
	 * 替换字符里的从指定的位置开始搜索出来的第一个字段，
	 * 
	 * @param target 目标{@link String}被搜索和替换
	 * @param searchString 要被查询的{@link String}
	 * @param replacement 替换的{@link String}
	 * @param fromIndex 从这个位置开始算起 0开始计数
	 * @return 替换完的字段
	 */
	public static final String replaceOnce(final String target, final String searchString, 
			final String replacement, final int fromIndex) {
		return replace(target, searchString, replacement, fromIndex, 1);
	}
	
	/**
	 * 替换字符里的从指定的位置开始搜索出来的所有字段，
	 * 
	 * @param target 目标{@link String}被搜索和替换
	 * @param searchString 要被查询的{@link String}
	 * @param replacement 替换的{@link String}
	 * @param fromIndex 从这个位置开始算起 0开始计数
	 * @return 替换完的字段
	 */
	public static final String replaceAll(final String target, final String searchString, 
			final String replacement, final int fromIndex) {
		return replace(target, searchString, replacement, fromIndex, -1);
	}
	
	/**
	 * 替换被限定在一组标记里面的所有子串{@link String}含标记，用{@code Map}中Key对应的值替换
	 * 
	 * @param target 目标{@link String}被搜索和替换
	 * @param openTag 子串开始的标记
	 * @param closeTag 子串关闭的标记
	 * @param map 含有要替换Value的{@code Map}，其中Key为大写
	 * @return 替换完的字段
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
	 * 替换被限定在{@code span}标签里的子串{@link String}不含标记，用{@code Map}中的Value替换<br>
	 * {@code span}的{@code id}为{@code Map}中的Key，Key对应的值将替换标签里的子串{@link String}<br>
	 * 格式为：&lt;span id='key'&gt;xxxxx&lt;/span&gt;
	 * 
	 * @param target 目标{@link String}被搜索和替换
	 * @param map 含有要替换Value的{@code Map}
	 * @return 替换完的字段
	 */
	public static final String replaceBetweenSpan(final String target, final Map<String, Object> map) {
		if (map.isEmpty()) {
			return target;
		}
		
		String replaceTarget = target;
		
		final Iterator<Entry<String, Object>> iterator =  map.entrySet().iterator();
		
		while (iterator.hasNext()) {
			final Entry<String, Object> entry = iterator.next();
			
			final String regex = "<span\\s+id\\s*=\\s*('|\")" + entry.getKey() + "('|\")\\s*>(.|\\s)*?</span>"; // 匹配的正则表达式
			
			final Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); // 不区分大小写
			
			final Matcher m = p.matcher(replaceTarget);
			
			replaceTarget = m.replaceAll("<span id='" + entry.getKey() + "'>" + ObjectUtils.toString(entry.getValue()) + "</span>");
		}
		
		return replaceTarget;
	}
	
	/**
	 * 删除开始的一部分{@link String}，如果有的话
	 * 
	 * @param target 目标{@link String}
	 * @param remove 要删除的{@link String}
	 * @return 删除完后{@link String}
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
	 * 删除结尾的一部分{@link String}，如果有的话
	 * 
	 * @param target 目标{@link String}
	 * @param remove 要删除的{@link String}
	 * @return 删除完后{@link String}
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
     * 判断{@link CharSequence} 的前缀是否是{@link CharSequence}，而且是不区分大小的
	 * 
	 * @param target 目标{@link CharSequence} 
	 * @param prefix 前缀{@link CharSequence}
	 * @return true 如果是目标的前缀
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
     * 判断{@link CharSequence} 的后缀是否是{@link CharSequence}，区分大小的
     * 
	 * @param target 目标{@link CharSequence} 
	 * @param suffix 前缀{@link CharSequence}
	 * @return true 如果是目标的前缀
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
     * 判断{@link CharSequence} 的后缀是否是{@link CharSequence}，而且是不区分大小的
     * 
	 * @param target 目标{@link CharSequence} 
	 * @param suffix 前缀{@link CharSequence}
	 * @return true 如果是目标的前缀
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
	 * 重复字符串来生成一个新的字符串
	 * 
	 * @param str 要重复的字符串
	 * @param repeat 重复的次数
	 * @return 重复后新的字符串
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
	 * 扩展{@link String#trim()}方法，删除开头和结尾的空格、回车、水平制表符、换行等都要去掉
	 * 
	 * @param target 目标{@link CharSequence}
	 * @return 替换后新的字符串
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
	 * 不用正则表达式的{@link String#split()}方法
	 * 
	 * @param target 目标{@link String}
	 * @param separator 分隔符
	 * @return 拆分成的字符串数组
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
	 * 不用正则表达式的获取字符串中匹配数量的方法
	 * 
	 * @param target 目标{@link String}
	 * @param match 要匹配的字符串
	 * @return 匹配的数量
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
	 * 判断{@link CharSequence} 是否为{@code null} 或全为数字字符
	 * @param cs cs  {@link CharSequence} 用于判断
	 * @return 如果是{@code null} 或者非数字字符 返回 false， 如果是数字字符返回 true
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
	 * 私有方法
	 *********************************************************************/
	/**
	 * 判断给定的{@link char}是否是空
	 * 
	 * @param c 用于比较的{@link char}
	 * @return true 如果是空，否则false
	 */
	private static final boolean isBlankChar(char c) {
		return (c == ' ' || c == '\t' || c == '\b' || c == '\r' || c == '\n' || c == '\f');
	}
}
