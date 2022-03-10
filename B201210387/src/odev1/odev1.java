/**
*
* @author Aleyna Elif ÖZKAN - aleyna.ozkan1@ogr.sakarya.edu.tr
* @since 06.04.2021
* <p>
* 	C++ dosyalarýný ayýrýp ekrana output bastýran program
* </p>
*/
package odev1;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class odev1{
	
public static void main (String[] args) {
	//dosyayý okuyorum
	
	String filePath = "./src/Program.cpp";
	String File = readFile(filePath);

	regex(File);
	
}
	 
public static String readFile(String filePath){
	// tüm dosyayý tek bir string halinde aldým ve bunu geri döndürdüm
	
	String content = "";
 
	try{
		content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
	}
	catch (IOException e){
		e.printStackTrace();
	}
		return content;
}

public static void regex(String File) {
	//parsing iþlemini regex kullanarak bu method içinde yaptým
	
	String PubString = null;
	
	Pattern patternClass = Pattern.compile("(?<=class)\\s*\\w+");
	//classlarý ayýrmak için regex
	
	Pattern patternPub = Pattern.compile("(?<=public:).+?(?=};)", Pattern.DOTALL);
	//public fonksiyonlarý ayýrmak için regex
	
	Pattern patternFunc = Pattern.compile("((.+?)(?=\\())(.*)(\\))\\s*((const)?(?=\\{))");
	//parametreleri ayýrmak için regex
	
	Matcher matcherClass = patternClass.matcher(File);
	Matcher matcherPub = patternPub.matcher(File);
	
	
	while(matcherPub.find() && matcherClass.find()){
		//public sýnýf bulunduðunda bastýr
		
		System.out.println("Sýnýf:" + matcherClass.group() + "\n");
			PubString = matcherPub.group();
		Matcher matcherFunc = patternFunc.matcher(PubString);
		
	    //fonksiyonlarý ayýrýp bastýrmak için helper methoda yolladým
	    while(matcherFunc.find()){
	    	print(matcherFunc.group(2).trim(),matcherFunc.group(),matcherFunc.group(3),matcherClass.group());
	    }
	}
	//super sýnýflarý ayýrýp bastýrmak için helper method
		superSinif(File);
}

public static void  print (String S, String ALL, String Par, String Class) {
	int index=0,indexAnd=0, spaces =0, parametre = 0;
	String helper= null, helper2=S;

	index = Par.indexOf(":");

	if(index!=-1) {
		// : varsa
		
		helper = Par.substring(0,index);
		//:ya kadar olan kýsýmý al
		
		parametre = parametreNum(helper);
		//parametre sayýsýný döndür
		
		index = S.indexOf("(");
		//( e kadar olan kýsýmý al
		
		System.out.println("\t"+ S);
    	System.out.print("\t\tParametre: "+ parametre );
    	
    	//parametreleri varsa onlarý bastýrmak için helper method
    	if(parametre>0) {
    		printParameters(helper.trim());
    		System.out.print(")");
    	}
    	
    	System.out.print("\n");
		System.out.print("\t\tDönüþ Türü: ");
		System.out.println("Nesne Adresi");	
	}
	
	
	//fonksiyon deðil de baþka bir keyword olabilme ihtimaline karþý if else 
	else if(S.trim().compareTo("if")!= 0 && S.trim().compareTo("switch")!= 0 && S.trim().compareTo("do")!= 0 && S.trim().compareTo("while")!= 0 && S.trim().compareTo("for")!= 0 ) {
		
		//eðer fonksiyon içinde & bulunuyorsa
		index = S.indexOf("&");
		indexAnd=index;
		
		spaces = S.trim().length() - S.trim().replaceAll(" ", "").length();
		
		
		if(spaces>=1 && indexAnd == -1) {
			
			//normal parametreli ise ilk kelimesi tipi 2. kelimesi ismidir
			parametre = parametreNum(ALL);
			
			//boþluklarýna göre ayýr
        	index = S.indexOf(" ");
        	
        	S = S.substring(index);
        	
    		System.out.println("\n\t" + S);
    		
    		if(parametre>0)
    			System.out.print("\t\tParametre: "+ parametre );
    		else
    			System.out.println("\t\tParametre: "+ parametre );

    		
        	if(parametre>0 && indexAnd==-1) {
    			printParameters(Par);
    			System.out.println(")");
    		}
    		
    
	    	System.out.print("\t\tDönüþ Türü: ");
	    	System.out.println(helper2.substring(0,index).trim());
    		
        }
		
		else if(spaces>1 && indexAnd != -1) {
        	//isimden önce iki kelime varsa ve & içeriyorsa
			
        	parametre = parametreNum(ALL);
        	S = S.substring(indexAnd+1);
        	
    		System.out.println("\n\t" + S);
        	System.out.print("\t\tParametre: "+ parametre);
        	
	        	if(parametre>0)
	        		printParameters(Par);
				
				if(parametre!=0)
					System.out.println(")");
			
			Pattern pattern = Pattern.compile("\\w+(?=&)");
			
			Matcher matcher = pattern.matcher(helper2);
			System.out.print("\n\t\tDönüþ Türü: ");
			
			while(matcher.find())
				System.out.print( matcher.group() +("&"));
			
				System.out.println("\n");
        }
		
       else {
        	//kurucu veya yýkýcý fonksiyon ise (dönüþ tipi yoksa)
        	parametre = parametreNum(ALL);
			System.out.println("\t" + S); 
			
	    	System.out.print("\t\tParametre: "+ parametre);
	    	
		    	if(parametre==0)
					System.out.print("\n");
		    	
				if(parametre!=0 && index==-1) {
					printParameters(Par);
					System.out.println(")");
				}
		
			System.out.print("\t\tDönüþ Türü: ");
			
			if(ALL.contains("~")==true)
				System.out.println("void");
			else
				System.out.println("Nesne Adresi");
       }
	}	
}


public static int parametreNum(String S) {
	int commas = 0;
	
	// parametreler ',' yardýmýyla ayrýldýðý için ona göre parçalanacak
	//virgül sayýsýnýn bir fazlasý kadar parametre vardýr
	
	   for(int i=0;i<S.length();i++){
	     if(S.charAt(i)==',')
	    	 commas++;
	   }
   
	   if(commas == 0 && ( S.indexOf("(")- S.indexOf(")") == -1))
		   return 0;
	   
	   if(commas == 0 && ( S.indexOf("(")- S.indexOf(")") != -1))
		   return 1;
	   
	   else
	   	return commas+1;
}

public static void printParameters(String S){
	int index=0;
	String str = null;

	//parametreler virgüle göre ayrýlýr
	//ayrýlan kýsýmda parametrenin ismi ve tipi vardýr ona göre parçalanýr
	
	Pattern pattern = Pattern.compile("(?<=\\()?([^,]+)");
	Matcher matcher = pattern.matcher(S);
	
	System.out.print(" ");

	for (int i = 0; matcher.find(); ++i) {
		
		  if (i > 0) 
			  System.out.print(", ");
		  
		  	str = matcher.group(1);
		  
		  if(str.contains("const")==true ) {
			 //parametrelerde const keywordü varsa
			  
			  index= str.indexOf("const") ;
			  str = str.substring(index+5,str.length());
			  
			  index= str.trim().indexOf(" ");
			  str = str.substring(0,index+1);
			  
			  System.out.print("(" + str.trim()); 
		  }
		  
		  else if(str.contains("<") == true && str.contains(">")==true){
			  //parametrelerde < veya > varsa
			  	Pattern pattern1 = Pattern.compile("(.*)?(?=<)(.*)(>)(\\*)?");
				
				Matcher matcher1 = pattern1.matcher(str);
				
				while(matcher1.find())
					System.out.print(matcher1.group(1) + matcher1.group(4));
					
		  }
		  
		  else {
			  
			  index= str.trim().indexOf(" ");
			  str = str.substring(0,index+1);
			  
			  System.out.print( str.trim()); 
		  }
	}
}

public static void superSinif(String File) {
	String helper=null;
	int commas=0;
	
	//süper sýnýflarý ayýrmak için regex
	
	System.out.println("\n\n" + "Süper Sýnýflar: ");
	
	    Pattern patternSup = Pattern.compile("(?<=class)(.+)(:)(.*)(?=\\{)");
    	Matcher matcherSup = patternSup.matcher(File);
    	
    	
	
	    while(matcherSup.find() ) {
	    	helper=matcherSup.group(3)+"{";
	    		
	    	if (helper.contains(",")==true) {
			   for(int i=0;i<helper.length();i++){
			     if(helper.charAt(i)==',')
			    	 commas++;
			   }
		   
	    	Pattern pattern = Pattern.compile("\\w+(?=,)");
	    	Matcher matcher = pattern.matcher(helper);
	
		    	while(matcher.find())
		    		System.out.println("\n\t\t" + matcher.group() +": " + commas);
		    	
		    	if(commas>0) {
		    		Pattern patternX = Pattern.compile("\\w+(?=\\{)");
			    	Matcher matcherX = patternX.matcher(helper);
			
		    	while(matcherX.find())
		    		System.out.println("\n\t\t" + matcherX.group() +": "+ commas);
    		
		    	}
	    	}
	    	else {
	    		
	    		commas=1;
	    		
	    		Pattern patternX = Pattern.compile("\\w+(?=\\{)");
		    	Matcher matcherX = patternX.matcher(helper);
		
	    	while(matcherX.find())
	    		System.out.println("\n\t\t" + matcherX.group() +": "+ commas);
	    		
	    	}
	    }
}

}