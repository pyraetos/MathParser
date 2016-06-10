package net.pyraetos.math;

public abstract class FunctionParser{

	private static double numberBuffer = 0.0d;
	private static char itemBuffer = 0;
	private static int trigOp = -1;
	private static String trigString = "";
	private static final byte NOP = 0;
	private static final byte PAR = 1;
	private static final byte TRI = 2;
	private static final byte POW = 3;
	private static final byte MUL = 4;
	private static final byte DIV = 5;
	private static final byte ADD = 6;
	private static final byte SUB = 7;

	public static Function parse(String input) throws ParseException{
		try{
			String left = "";
			String right = "";
			byte currentOp = NOP;
			boolean impMul = false;
			while(!input.isEmpty()){
				if(isNext(' ', input))
					input = parseItem(input);
				else
				if(isTrigNext(input)){
					if(impMul){
						input = "*" + input;
						impMul = false;
						continue;
					}
					input = parseTrig(input);
					if(currentOp < TRI){
						currentOp = TRI;
					}
					else
						right += trigString;
					impMul = false;
				}
				else
				if(isNext('(', input)){
					if(impMul){
						input = "*" + input;
						impMul = false;
						continue;
					}
					input = parseItem(input);
					int parLevel = 1;
					if(currentOp < POW){
						while(parLevel > 0){
							if(isNext('(', input)) parLevel++;
							if(isNext(')', input)) parLevel--;
							input = parseItem(input);
							if(parLevel > 0) left += itemBuffer;
						}
						currentOp = currentOp > PAR ? currentOp : PAR;
					}
					else
						while(parLevel > 0){
							if(isNext('(', input)) parLevel++;
							if(isNext(')', input)) parLevel--;
							input = parseItem(input);
							if(parLevel > 0) right += itemBuffer;
						}
					impMul = true;
				}
				else
				if(isNumberNext(input)){
					if(impMul){
						input = "*" + input;
						impMul = false;
						continue;
					}
					input = parseNumber(input);
					if(currentOp < POW)
						left += numberBuffer;
					else
						right += numberBuffer;
					impMul = true;
				}
				else
				if(isXNext(input)){
					if(impMul){
						input = "*" + input;
						impMul = false;
						continue;
					}
					input = parseItem(input);
					if(currentOp < POW)
						left += 'x';
					else
						right += 'x';
					impMul = true;
				}
				else
				if(isOpNext(input)){
					input = parseItem(input);
					switch(itemBuffer){
					case '^': {
						if(currentOp < POW){
							left = currentOp == TRI ? trigString + "(" + left + ")" : left + opFromCode(currentOp) + right;
							right = "";
							currentOp = POW;
						}
						else
							right += '^';
						break;
					}
					case '*': {
						if(currentOp < MUL){
							left = currentOp == TRI ? trigString + "(" + left + ")" : left + opFromCode(currentOp) + right;
							right = "";
							currentOp = MUL;
						}
						else
							right += '*';
						break;
					}
					case '/': {
						if(currentOp < MUL){
							left = currentOp == TRI ? trigString + "(" + left + ")" : left + opFromCode(currentOp) + right;
							right = "";
							currentOp = DIV;
						}
						else
							right += '/';
						break;
					}
					case '+': {
						if(currentOp < ADD){
							left = currentOp == TRI ? trigString + "(" + left + ")" : left + opFromCode(currentOp) + right;
							right = "";
							currentOp = ADD;
						}
						else
							right += '+';
						break;
					}
					case '-': {
						if(currentOp < ADD){
							left = currentOp == TRI ? trigString + "(" + left + ")" : left + opFromCode(currentOp) + right;
							if(left.equals("")) left = "0";
							right = "";
							currentOp = SUB;
						}
						else
							right += '-';
						break;
					}
					}
					impMul = false;
				}else throw new ParseException();
			}
			Function function = null;
			if(currentOp == NOP)
				function = left.equals("x") ? new Function.IndependentVariable() : new Function.Constant(Double.parseDouble(left));
				else if(currentOp == TRI)
					function = new Function.Trig(trigOp, parse(left));
				else if(currentOp == PAR)
					function = parse(left);
				else if(currentOp == POW)
					function = new Function.Power(parse(left), parse(right));
				else if(currentOp == MUL)
					function = new Function.Multiplication(parse(left), parse(right));
				else if(currentOp == DIV)
					function = new Function.Division(parse(left), parse(right));
				else if(currentOp == ADD)
					function = new Function.Addition(parse(left), parse(right));
				else if(currentOp == SUB)
					function = new Function.Subtraction(parse(left), parse(right));
			return function;
		}catch(Exception e){
			throw new ParseException();
		}
	}

	private static boolean isTrigNext(String input){
		return input.startsWith("sin")
				|| input.startsWith("cos")
			|| input.startsWith("tan")
			|| input.startsWith("asin")
			|| input.startsWith("acos")
			|| input.startsWith("atan")
			|| input.startsWith("ln")
			|| input.startsWith("log")
			|| input.startsWith("sqrt");
	}
	
	private static String parseTrig(String input){
		if(input.startsWith("sin")){
			trigOp = Function.Trig.SIN;
			trigString = "sin";
			return input.substring(3);
		}
		if(input.startsWith("cos")){
			trigOp = Function.Trig.COS;
			trigString = "cos";
			return input.substring(3);
		}
		if(input.startsWith("tan")){
			trigOp = Function.Trig.TAN;
			trigString = "tan";
			return input.substring(3);
		}
		if(input.startsWith("asin")){
			trigOp = Function.Trig.ASIN;
			trigString = "asin";
			return input.substring(4);
		}
		if(input.startsWith("acos")){
			trigOp = Function.Trig.ACOS;
			trigString = "acos";
			return input.substring(4);
		}
		if(input.startsWith("atan")){
			trigOp = Function.Trig.ATAN;
			trigString = "atan";
			return input.substring(4);
		}
		if(input.startsWith("ln")){
			trigOp = Function.Trig.LN;
			trigString = "ln";
			return input.substring(2);
		}
		if(input.startsWith("log")){
			trigOp = Function.Trig.LN;
			trigString = "ln";
			return input.substring(3);
		}
		if(input.startsWith("sqrt")){
			trigOp = Function.Trig.SQRT;
			trigString = "sqrt";
			return input.substring(4);
		}
		return null;
	}
	
	private static String opFromCode(byte code){
		switch(code){
		case POW: return "^";
		case MUL: return "*";
		case DIV: return "/";
		case ADD: return "+";
		case SUB: return "-";
		}
		return "";
	}
	
	private static boolean isOpNext(String input){
		if(input.isEmpty()) return false;
		char c = input.charAt(0);
		switch(c){
		case '^':
		case '*':
		case '/':
		case '+':
		case '-': return true;
		default: return false;
		}
	}
	
	private static boolean isNext(char a, String input){
		if(input.isEmpty()) return false;
		return input.charAt(0) == a;
	}
	
	private static boolean isXNext(String input){
		if(input.isEmpty()) return false;
		char c = input.charAt(0);
		return c == 'x';
	}
	
	private static String parseItem(String input){
		itemBuffer = input.charAt(0);
		return input.substring(1);
	}
	
	private static boolean isNumberNext(String input){
		if(input.isEmpty()) return false;
		char c = input.charAt(0);
		return  c == 'e' || c == '.' || isDigit(c);
	}
	
	private static String parseNumber(String input){
		String number = "";
		char c = input.charAt(0);
		if(c == 'e'){
			numberBuffer = Math.E;
			return input.substring(1);
		}
		boolean periodUsed = false;
		do{
			number += c;
			if(c == '.'){
				if(periodUsed)
					return null;
				periodUsed = true;
			}
			input = input.substring(1);
			if(input.isEmpty()) break;
			c = input.charAt(0);
		}while(isDigit(c) || c == '.');
		numberBuffer = Double.parseDouble(number);
		return input;
	}
	
	private static boolean isDigit(char c){
		return c == '0' || c == '1' || c == '2' || c == '3' || c == '4'
				|| c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
	}
	
}
