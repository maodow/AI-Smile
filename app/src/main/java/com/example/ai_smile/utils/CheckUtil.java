package com.example.ai_smile.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Class CheckUtil
 * @Description 校验工具类 
 * @Author YuanLiangfeng
 * @Date 2016-6-8 下午3:41:33
 */
public class CheckUtil {

	private static CheckUtil checkUtil;

	public static CheckUtil getInstance(){
		if(checkUtil == null){
			checkUtil = new CheckUtil();
		}
		return checkUtil;
	}
	
	/**
	 * @Description: 校验车辆号码
	 * @param vehicleNumber
	 * @return
	 */
	public boolean checkVehicleNumber(String vehicleNumber){
		String regex = "^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$";
		if (TextUtils.isEmpty(vehicleNumber)){
			return false;
		}else{
			return vehicleNumber.matches(regex);
		}
	}
	
	/**
	 * @Description 校验发动机号,规则：包含字母,数字,'-','*'5至30位之间 
	 * @param engineNumber
	 * @return
	 */
	public boolean checkEngineNumber(String engineNumber){
		String regex = "[a-zA-Z0-9\\-\\*]{5,30}";
		if (TextUtils.isEmpty(engineNumber)){
			return false;
		}else{
			return engineNumber.matches(regex);
		}
	}

	
	/**
	 * @Description: 手机号验证
	 */
	public boolean phoneNumCheck(String phoneNum){
		//"[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (phoneNum.length() != 11){
			return false;
		}else {
			String telRegex = "[1][356789]\\d{9}";
			return phoneNum.matches(telRegex);
		}
	}

	//车辆识别号VIN中字母对应的数值
	public Hashtable<String, Integer> htbVIN(){
        Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
        ht.put("A", 1);
        ht.put("B", 2);
        ht.put("C", 3);
        ht.put("D", 4);
        ht.put("E", 5);
        ht.put("F", 6);
        ht.put("G", 7);
        ht.put("H", 8);
        
        ht.put("J", 1);
        ht.put("K", 2);
        ht.put("L", 3);
        ht.put("M", 4);
        ht.put("N", 5);
        ht.put("P", 7);
        ht.put("R", 9);
        ht.put("S", 2);
        ht.put("T", 3);
        ht.put("U", 4);
        ht.put("V", 5);
        ht.put("W", 6);
        ht.put("X", 7);
        ht.put("Y", 8);
        ht.put("Z", 9);
        
        ht.put("1", 1);
        ht.put("2", 2);
        ht.put("3", 3);
        ht.put("4", 4);
        ht.put("5", 5);
        ht.put("6", 6);
        ht.put("7", 7);
        ht.put("8", 8);
        ht.put("9", 9);
        ht.put("0", 0);

        return ht;
    }


    //车辆识别号中顺序对应的加权系数
	public Hashtable<Integer, Integer> htbVIN_JQS(){
        Hashtable<Integer, Integer> ht = new Hashtable<Integer, Integer>();
        ht.put(1, 8);
        ht.put(2, 7);
        ht.put(3, 6);
        ht.put(4, 5);
        ht.put(5, 4);
        ht.put(6, 3);
        ht.put(7, 2);
        ht.put(8, 10);
        ht.put(9, 0);
        ht.put(10, 9);
        ht.put(11, 8);
        ht.put(12, 7);
        ht.put(13, 6);
        ht.put(14, 5);
        ht.put(15, 4);
        ht.put(16, 3);
        ht.put(17, 2);
        
        return ht;
    }

    // IN可用字符
    public String sKYZF="ABCDEFGHJKLMNPRSTUVWXYZ1234567890";
    
    //检验车辆识别号
	public boolean getCheckCode_VIN(String sVIN){
    	String sJYW = "";
    	boolean bl = false;
    	boolean blKYZF = false;
        if (sVIN.length() == 17){
            int iJQS=0 ,intTemp=0;
            Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
            ht = htbVIN();
            Hashtable<Integer, Integer> htZM = new Hashtable<Integer, Integer>();
            htZM = htbVIN_JQS();
            if(!sVIN.startsWith("L")){
            	bl = false;
            }
            try{
                for (int i = 0; i < sVIN.length(); i++){
                    if (sKYZF.contains (sVIN.substring(i, i+1))){
                        blKYZF = true;
                        int temp1 = (int)ht.get(sVIN.substring(i, i+1));
                        int temp2 = (int)htZM.get(i+1);
                        iJQS = iJQS + temp1 * temp2;
                    }else{
                        blKYZF = false;
                        break;//发现不合法字符，直接退出循环                            
                    }
                }
                if (blKYZF){
                    intTemp = iJQS % 11;
                    if (intTemp == 10)
                        sJYW = "X";
                    else
                        sJYW = intTemp+"";
                    	String temp = sVIN.charAt(8)+"";
                    if (sJYW.equals(temp))
                        bl = true;
                }else{
                    bl = false;
                }
            }catch(Exception e){
                bl = false;
            }
        }
        return bl;
    }
    
    /** 
     * 校验银行卡卡号 
     * @param cardId 
     * @return 
     */  
    public boolean checkBankCard(String cardId) {
          char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));  
          if(bit == 'N'){  
              return false;  
          }  
          return cardId.charAt(cardId.length() - 1) == bit;  
    }  
     
    
    /** 
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位 
     * @param nonCheckCodeCardId 
     * @return 
     */  
    private char getBankCardCheckCode(String nonCheckCodeCardId){
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0  
                || !nonCheckCodeCardId.matches("\\d+")) {  
         //如果传的不是数据返回N  
            return 'N';  
        }  
        char[] chs = nonCheckCodeCardId.trim().toCharArray();  
        int luhmSum = 0;  
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {  
            int k = chs[i] - '0';  
            if(j % 2 == 0) {  
                k *= 2;  
                k = k / 10 + k % 10;  
            }  
            luhmSum += k;             
        }  
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');  
    }  
    
    
    public boolean isEmail(String email){
        String str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
           Pattern p = Pattern.compile(str);
           Matcher m = p.matcher(email);
           return m.matches();     
       } 
    
    public boolean nameCheck(String name){
    	String checkStr = "[\u4e00-\u9fa5]{2,4}";
    	Pattern p = Pattern.compile(checkStr);
        Matcher m = p.matcher(name);
        return m.matches();
    }
    
    /**
     * @Description 身份证校验 
     * @param idcard 身份证号码
     * @return
     */
    public boolean persionNumberCheck(String idcard){
    	boolean isBoolean = false;
    	if(idcard.length() == 18){
    		isBoolean = isValidate18Idcard(idcard);
    	}
    	if(idcard.length() == 15){
    		isBoolean = isValidate15Idcard(idcard);
    	}
    	return isBoolean;
    }
    
    /**
     * 身份证前6位【ABCDEF】为行政区划数字代码（简称数字码）说明（参考《GB/T 2260-2007 中华人民共和国行政区划代码》）：
     * 该数字码的编制原则和结构分析，它采用三层六位层次码结构，按层次分别表示我国各省（自治区，直辖市，特别行政区）、
     * 市（地区，自治州，盟）、县（自治县、县级市、旗、自治旗、市辖区、林区、特区）。 
     数字码码位结构从左至右的含义是： 
     第一层为AB两位代码表示省、自治区、直辖市、特别行政区； 
     第二层为CD两位代码表示市、地区、自治州、盟、直辖市所辖市辖区、县汇总码、省（自治区）直辖县级行政区划汇总码，其中： 
     ——01~20、51~70表示市，01、02还用于表示直辖市所辖市辖区、县汇总码； 
     ——21~50表示地区、自治州、盟； 
     ——90表示省（自治区）直辖县级行政区划汇总码。 
     第三层为EF两位表示县、自治县、县级市、旗、自治旗、市辖区、林区、特区，其中： 
     ——01~20表示市辖区、地区（自治州、盟）辖县级市、市辖特区以及省（自治区）直辖县级行政区划中的县级市，01通常表示辖区汇总码；
     ——21~80表示县、自治县、旗、自治旗、林区、地区辖特区； 
     ——81~99表示省（自治区）辖县级市。 
    * <p>
     * 类说明:身份证合法性校验
     * </p>
     * <p>
     * --15位身份证号码：第7、8位为出生年份(两位数)，第9、10位为出生月份，第11、12位代表出生日期，第15位代表性别，奇数为男，偶数为女。
     * --18位身份证号码
     * ：第7、8、9、10位为出生年份(四位数)，第11、第12位为出生月份，第13、14位代表出生日期，第17位代表性别，奇数为男，偶数为女。
     * </p>
     */
    protected String codeAndCity[][] = { { "11", "北京" }, { "12", "天津" },
			{ "13", "河北" }, { "14", "山西" }, { "15", "内蒙古" }, { "21", "辽宁" },
			{ "22", "吉林" }, { "23", "黑龙江" }, { "31", "上海" }, { "32", "江苏" },
			{ "33", "浙江" }, { "34", "安徽" }, { "35", "福建" }, { "36", "江西" },
			{ "37", "山东" }, { "41", "河南" }, { "42", "湖北" }, { "43", "湖南" },
			{ "44", "广东" }, { "45", "广西" }, { "46", "海南" }, { "50", "重庆" },
			{ "51", "四川" }, { "52", "贵州" }, { "53", "云南" }, { "54", "西藏" },
			{ "61", "陕西" }, { "62", "甘肃" }, { "63", "青海" }, { "64", "宁夏" },
			{ "65", "新疆" }, { "71", "台湾" }, { "81", "香港" }, { "82", "澳门" },
			{ "91", "国外" } };

	private String cityCode[] = { "11", "12", "13", "14", "15", "21", "22",
			"23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
			"44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63",
			"64", "65", "71", "81", "82", "91" };

	// 每位加权因子
	private int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	// 第18位校检码
	private String verifyCode[] = { "1", "0", "X", "9", "8", "7", "6", "5",
			"4", "3", "2" };

	/**
	 * <p>
	 * 判断18位身份证的合法性
	 * </p>
	 * 根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
	 * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
	 * <p>
	 * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
	 * </p>
	 * <p>
	 * 1.前1、2位数字表示：所在省份的代码； 2.第3、4位数字表示：所在城市的代码； 3.第5、6位数字表示：所在区县的代码；
	 * 4.第7~14位数字表示：出生年、月、日； 5.第15、16位数字表示：所在地的派出所的代码；
	 * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
	 * 7.第18位数字是校检码：也有的说是个人信息码，一般是随计算机的随机产生，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示。
	 * </p>
	 * <p>
	 * 第十八位数字(校验码)的计算方法为： 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4
	 * 2 1 6 3 7 9 10 5 8 4 2
	 * </p>
	 * <p>
	 * 2.将这17位数字和系数相乘的结果相加。
	 * </p>
	 * <p>
	 * 3.用加出来和除以11，看余数是多少？
	 * </p>
	 * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3
	 * 2。
	 * <p>
	 * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
	 * </p>
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isValidate18Idcard(String idcard) {
		// 非18位为假
		if (idcard.length() != 18) {
			return false;
		}
		// 获取前17位
		String idcard17 = idcard.substring(0, 17);
		// 获取第18位
		String idcard18Code = idcard.substring(17, 18);
		char c[] = null;
		String checkCode = "";
		// 是否都为数字
		if (isDigital(idcard17)) {
			c = idcard17.toCharArray();
		} else {
			return false;
		}

		if (null != c) {
			int bit[] = new int[idcard17.length()];

			bit = converCharToInt(c);

			int sum17 = 0;

			sum17 = getPowerSum(bit);

			// 将和值与11取模得到余数进行校验码判断
			checkCode = getCheckCodeBySum(sum17);
			if (null == checkCode) {
				return false;
			}
			// 将身份证的第18位与算出来的校码进行匹配，不相等就为假
			if (!idcard18Code.equalsIgnoreCase(checkCode)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证15位身份证的合法性,该方法验证不准确，最好是将15转为18位后再判断，该类中已提供。
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isValidate15Idcard(String idcard) {
		// 非15位为假
		if (idcard.length() != 15) {
			return false;
		}

		// 是否全都为数字
		if (isDigital(idcard)) {
			String provinceid = idcard.substring(0, 2);
			String birthday = idcard.substring(6, 12);
			int year = Integer.parseInt(idcard.substring(6, 8));
			int month = Integer.parseInt(idcard.substring(8, 10));
			int day = Integer.parseInt(idcard.substring(10, 12));

			// 判断是否为合法的省份
			boolean flag = false;
			for (String id : cityCode) {
				if (id.equals(provinceid)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				return false;
			}
			// 该身份证生出日期在当前日期之后时为假
			Date birthdate = null;
			try {
				birthdate = new SimpleDateFormat("yyMMdd", Locale.getDefault()).parse(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (birthdate == null || new Date().before(birthdate)) {
				return false;
			}

			// 判断是否为合法的年份
			GregorianCalendar curDay = new GregorianCalendar();
			int curYear = curDay.get(Calendar.YEAR);
			int year2bit = Integer.parseInt(String.valueOf(curYear)
					.substring(2));

			// 判断该年份的两位表示法，小于50的和大于当前年份的，为假
			if ((year < 50 && year > year2bit)) {
				return false;
			}

			// 判断是否为合法的月份
			if (month < 1 || month > 12) {
				return false;
			}

			// 判断是否为合法的日期
			boolean mflag = false;
			curDay.setTime(birthdate); // 将该身份证的出生日期赋于对象curDay
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				mflag = (day >= 1 && day <= 31);
				break;
			case 2: // 公历的2月非闰年有28天,闰年的2月是29天。
				if (curDay.isLeapYear(curDay.get(Calendar.YEAR))) {
					mflag = (day >= 1 && day <= 29);
				} else {
					mflag = (day >= 1 && day <= 28);
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				mflag = (day >= 1 && day <= 30);
				break;
			}
			if (!mflag) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 数字验证
	 * 
	 * @param str
	 * @return
	 */
	public boolean isDigital(String str) {
		return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");
	}
	
	
	/**
	 * 将字符数组转为整型数组
	 * 
	 * @param c
	 * @return
	 * @throws NumberFormatException
	 */
	public int[] converCharToInt(char[] c) throws NumberFormatException {
		int[] a = new int[c.length];
		int k = 0;
		for (char temp : c) {
			a[k++] = Integer.parseInt(String.valueOf(temp));
		}
		return a;
	}
	
	
	/**
	 * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
	 * 
	 * @param bit
	 * @return
	 */
	public int getPowerSum(int[] bit) {

		int sum = 0;

		if (power.length != bit.length) {
			return sum;
		}

		for (int i = 0; i < bit.length; i++) {
			for (int j = 0; j < power.length; j++) {
				if (i == j) {
					sum = sum + bit[i] * power[j];
				}
			}
		}
		return sum;
	}
	
	
	/**
	 * 将和值与11取模得到余数进行校验码判断
	 * 
	 * @param
	 * @param sum17
	 * @return 校验位
	 */
	public String getCheckCodeBySum(int sum17) {
		String checkCode = null;
		switch (sum17 % 11) {
		case 10:
			checkCode = "2";
			break;
		case 9:
			checkCode = "3";
			break;
		case 8:
			checkCode = "4";
			break;
		case 7:
			checkCode = "5";
			break;
		case 6:
			checkCode = "6";
			break;
		case 5:
			checkCode = "7";
			break;
		case 4:
			checkCode = "8";
			break;
		case 3:
			checkCode = "9";
			break;
		case 2:
			checkCode = "x";
			break;
		case 1:
			checkCode = "0";
			break;
		case 0:
			checkCode = "1";
			break;
		}
		return checkCode;
	}

	/**
	 * @Description 校验密码：6-20位数字或字母
	 * @param password
	 * @return
	 */
	public boolean checkPassword(String password){
		String regex = "^[0-9A-Za-z]{6,20}$";
		// 8-20位数字和字母组合
//		String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$";
		if (TextUtils.isEmpty(password)){
			return false;
		}else{
			return password.matches(regex);
		}
	}
}