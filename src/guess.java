import java.math.BigInteger;


public class guess {
	public static BigInteger findNumber(BigInteger c) {
        // implement this...
    	BigInteger i=  BigInteger.valueOf(10);
    	//BigInteger k=  BigInteger.valueOf(10);
    	BigInteger y=  BigInteger.valueOf(100);
    	BigInteger z=  BigInteger.valueOf(1);
    	
    	BigInteger a=  BigInteger.valueOf(2);
    	int f=0;
    	
    	if(guess(z).equals("correct")) {
    		System.out.println(f+" out");
    		return z;
    	}
    	
    	String check ="";
    	check =guess(i);
    	boolean goon=true;
        while(!check.equals("correct")) {
        	
        	
        	System.out.println(f+" out");
        	if(check.equals("correct")) {
        		return i;
        	}
        	if(check.equals("lower")) {
        		goon=false;
        		y=i;
        		if(i.divide(BigInteger.valueOf(5)).compareTo(z)==0) {
        			
        			i=i.divide(a);
        		}else {
        			i=i.subtract(i.subtract(z).divide(a));
        		}
        		//i=((y.subtract(i)).divide(BigInteger.valueOf(2))).add(i);
        		/*String check1 =guess(z);
        		if(check1.equals("correct")) {
        		
        		return z;
        		}else if(check1.equals("higher")) {
        			y=z;
        		}else {
        			i=z;*/
        			
        		//}
        	} 
        	else 
        		if(check.equals("higher")) {
        			if(goon) {
        		y=y.multiply(BigInteger.valueOf(5));
            	i=i.multiply(BigInteger.valueOf(5));
            	z=z.multiply(BigInteger.valueOf(5));
            	//k=i;
            	//check= guess(k);
            	System.out.println(f+" hello");
        		}else {
        			z=i;
        			i=i.add(y.subtract(i).divide(a));
        		}
            	}
        	
        	
        	f=f+1;
        	check =guess(i);
        }
        
       return i;
    }
	public static String guess(BigInteger i) {
		BigInteger chose = new BigInteger("2");
		//System.out.println(chose+" E");
		String k="";
		if(i.equals(chose)) {
			k= "correct";
		} else if(i.compareTo(chose) > 0 ) {
			k= "lower";
		} else if(i.compareTo(chose)<0) {
			k= "higher";
		}
		//System.out.println(k);
		return k;
		
	}
	public static void main(String[] agrs) {
		System.out.println(findNumber(BigInteger.valueOf(5900)));
	}
}
