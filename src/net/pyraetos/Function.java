package net.pyraetos;

public abstract class Function{
	
	protected Function inner;
	
	public void compose(Function inner){
		this.inner = inner;
	}
	
	public double evaluate(double x){
		return inner.evaluate(x);
	}
	
	public Point[] evaluate(double xMin, double xMax, double xStep){
		int n = (int) ((xMax - xMin) / xStep);
		Point[] points = new Point[n+1];
		for(int i = 0; i <= n; i++){
			double x = xMin + i * xStep;
			points[i] = new Point(x, evaluate(x));
		}
		return points;
	}
	
	static class Constant extends Function{
		
		private double value;
		
		public Constant(double value){
			this.value = value;
		}
		
		@Override
		public double evaluate(double x){
			return value;
		}
		
	}
	
	static class IndependentVariable extends Function{

		@Override
		public double evaluate(double x){
			return x;
		}

	}
	
	static class Trig extends Function{
		
		static final int SIN = 0;
		static final int COS = SIN + 1;
		static final int TAN = COS + 1;
		static final int ASIN = TAN + 1;
		static final int ACOS = ASIN + 1;
		static final int ATAN = ACOS + 1;
		static final int LN = ATAN + 1;
		static final int SQRT = LN + 1;
		
		private int op;
		
		public Trig(int op, Function inner){
			this.inner = inner;
			this.op = op;
		}
		
		@Override
		public double evaluate(double x){
			switch(op){
			case SIN: return Math.sin(inner.evaluate(x));
			case COS: return Math.cos(inner.evaluate(x));
			case TAN: return Math.tan(inner.evaluate(x));
			case ASIN: return Math.asin(inner.evaluate(x));
			case ACOS: return Math.acos(inner.evaluate(x));
			case ATAN: return Math.atan(inner.evaluate(x));
			case LN: return Math.log(inner.evaluate(x));
			case SQRT: return Math.sqrt(inner.evaluate(x));
			}
			return 0;
		}
		
	}
	
	static class Power extends Function{
		
		private Function right;

		public Power(Function left, Function right){
			this.inner = left;
			this.right = right;
		}

		@Override
		public double evaluate(double x){
			return Math.pow(inner.evaluate(x), right.evaluate(x));
		}
		
	}

	static class Multiplication extends Function{

		private Function right;

		public Multiplication(Function left, Function right){
			this.inner = left;
			this.right = right;
		}

		@Override
		public double evaluate(double x){
			return inner.evaluate(x) * right.evaluate(x);
		}

	}
	
	static class Division extends Function{

		private Function right;

		public Division(Function left, Function right){
			this.inner = left;
			this.right = right;
		}

		@Override
		public double evaluate(double x){
			return inner.evaluate(x) / right.evaluate(x);
		}

	}
	
	static class Addition extends Function{
		
		private Function right;
		
		public Addition(Function left, Function right){
			this.inner = left;
			this.right = right;
		}
		
		@Override
		public double evaluate(double x){
			return inner.evaluate(x) + right.evaluate(x);
		}
		
	}
	
	static class Subtraction extends Function{
		
		private Function right;
		
		public Subtraction(Function left, Function right){
			this.inner = left;
			this.right = right;
		}
		
		@Override
		public double evaluate(double x){
			return inner.evaluate(x) - right.evaluate(x);
		}
		
	}
}
