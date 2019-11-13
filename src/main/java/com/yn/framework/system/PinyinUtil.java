package com.yn.framework.system;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by youjiannuo on 17/2/14.
 */

public class PinyinUtil {

    public static String[] chineseToSpell(char c) {
        HanyuPinyinOutputFormat spellFormat = new HanyuPinyinOutputFormat();
        spellFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        spellFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        spellFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        String[] arry;
        try {
            arry = PinyinHelper.toHanyuPinyinStringArray(c, spellFormat);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
            return new String[]{c + ""};
        }
        return arry;
    }

    public static char chineseToSpellFirst(char c) {
        String array[] = chineseToSpell(c);
        return array == null ? c : array[0].charAt(0);
    }

    public static String chineseToSpellString(char c) {
        String array[] = chineseToSpell(c);
        String s = "";
        for (String a : array) {
            s += a;
        }
        return s;
    }


}
