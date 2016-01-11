package Examples;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import Examples.PhoneLibrary;
import Examples.Phone;
import Examples.Customer;
import Examples.Stock;
import Examples.Tablet;

import Examples.SimpleGUI;

public class Reasoner {

	// The main Class Object holding the Domain knowledge

	public PhoneLibrary thephonelibrary; 

	public SimpleGUI Myface;

	// The lists holding the class instances of all domain entities

	public List thePhoneLibraryList = new ArrayList(); 
	public List thePhoneList = new ArrayList();   
	public List theCustomerList = new ArrayList(); 
	public List theStockList = new ArrayList();  
	public List theRecentThing = new ArrayList();
	public List theReviewList = new ArrayList();
	public List theTabletList = new ArrayList();     
	public static int error = 0;
	// Gazetteers to store synonyms for the domain entities names

	public Vector<String> phonelibrarysyn = new Vector<String>();   
	public Vector<String> phonesyn = new Vector<String>();      
	public Vector<String> customersyn = new Vector<String>();    
	public Vector<String> reviewsyn = new Vector<String>();   
	public Vector<String> stocksyn = new Vector<String>();  
	public Vector<String> recentobjectsyn = new Vector<String>();
	public Vector<String> tabletsyn = new Vector<String>();     

	public String questiontype = "";         // questiontype selects method to use in a query
	public List classtype = new ArrayList(); // classtype selects which class list to query
	public String attributetype = "";        // attributetype selects the attribute to check for in the query

	public Object Currentitemofinterest; // Last Object dealt with
	public Integer Currentindex;         // Last Index used

	public String tooltipstring = "";
	public String URL = "";              // URL for Wordnet site
	public String URL2 = "";             // URL for Wikipedia entry

	public Reasoner(SimpleGUI myface) {

		Myface = myface; // reference to GUI to update Tooltip-Text
		// basic constructor for the constructors sake :)
	}

	public void initknowledge() { // load all the phonelibrary knowledge from XML 

		JAXB_XMLParser xmlhandler = new JAXB_XMLParser(); // we need an instance of our parser

		 
		File xmlfiletoload = new File("PhoneLibrary.xml"); // we need a (CURRENT)  file (xml) to load  

		// Init synonmys and typo forms in gazetteers

		phonelibrarysyn.add("things");   	 
		phonelibrarysyn.add("place");		 
		phonelibrarysyn.add("phonestore");	 
		phonelibrarysyn.add("phonehouse"); 	 
		phonelibrarysyn.add("store");		 
		phonelibrarysyn.add("house");		 
		phonelibrarysyn.add("offer");		 
		phonelibrarysyn.add("store");		 

		phonesyn.add("phone");    //All of the following is a candidate for a name change
		phonesyn.add("pone");
		phonesyn.add("phone");
		phonesyn.add("celular");
		phonesyn.add("talker");
		phonesyn.add("smartphone");
		phonesyn.add("brick");
		phonesyn.add("smarthpone");
		
		tabletsyn.add("tablet");    //All of the following is a candidate for a name change
		tabletsyn.add("tab");
		tabletsyn.add("tabblet");


		customersyn.add("customer"); //All of the following is a candidate for a name change
		customersyn.add("custome");
		customersyn.add("client");
		customersyn.add("dude");
		customersyn.add("man");
		customersyn.add("guy");
		customersyn.add("babe");
		customersyn.add("darling");

		reviewsyn.add("review");    
		reviewsyn.add("thinghy");
		reviewsyn.add("revew");

		stocksyn.add("stock");   //All of the following is a candidate for a name change

		recentobjectsyn.add(" this");   //All of the following is a candidate for a name change
		recentobjectsyn.add(" that");
		recentobjectsyn.add(" him");
		recentobjectsyn.add(" her");	// spaces to prevent collision with "wHERe"	
		recentobjectsyn.add(" it");

		try {
			FileInputStream readthatfile = new FileInputStream(xmlfiletoload); // initiate input stream

			thephonelibrary = xmlhandler.loadXML(readthatfile);

			// Fill the Lists with the objects data just generated from the xml

			thePhoneList = thephonelibrary.getPhone();  		 
			theCustomerList = thephonelibrary.getCustomer(); 	 
			theStockList = thephonelibrary.getStock(); 	 
			theTabletList = thephonelibrary.getTablet();  		 
			theReviewList = thephonelibrary.getReview();  
			thePhoneLibraryList.add(thephonelibrary);             // force it to be a List,  

			System.out.println("List reading");
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in init");
		}
	}

	public  Vector<String> generateAnswer(String input) { // Generate an answer (String Vector)
		
		Vector<String> out = new Vector<String>();
		out.clear();                 // just to make sure this is a new and clean vector
		
		questiontype = "none";

		Integer Answered = 0;        // check if answer was generated

		Integer subjectcounter = 0;  // Counter to keep track of # of identified subjects (classes)
		
		

		// ___________________________ IMPORTANT _____________________________

		input = input.toLowerCase(); // all in lower case because thats easier to analyse
		
		// ___________________________________________________________________

		String answer = "";          // the answer we return

		// ----- Check for the kind of question (number, description, etc)------------------------------

		if (input.contains("how many")){questiontype = "amount"; input = input.replace("how many", "<b>how many</b>");} 
		if (input.contains("number of")){questiontype = "amount"; input = input.replace("number of", "<b>number of</b>");}
		if (input.contains("amount of")){questiontype = "amount"; input = input.replace("amount of", "<b>amount of</b>");} 
		if (input.contains("count")){questiontype = "amount"; input = input.replace("count", "<b>count</b>");}

		if (input.contains("what kind of")){questiontype = "list"; input = input.replace("what kind of", "<b>what kind of</b>");}
		if (input.contains("list all")){questiontype = "list"; input = input.replace("list all", "<b>list all</b>");}
		if (input.contains("diplay all")){questiontype = "list"; input = input.replace("diplay all", "<b>diplay all</b>");}
		if (input.contains("show all")){questiontype = "list"; input = input.replace("show all", "<b>show all</b>");}
		if (input.contains("find all")){questiontype = "list"; input = input.replace("find all", "<b>find all</b>");}
		
		if (input.contains("is there a")){questiontype = "checkfor"; input = input.replace("is there a", "<b>is there a</b>");}
		if (input.contains("i am searching")){questiontype = "checkfor"; input = input.replace("i am searching", "<b>i am searching</b>");}
		if (input.contains("i am looking for")){questiontype = "checkfor"; input = input.replace("i am looking for", "<b>i am looking for</b>");}
		if (input.contains("do you have")&&!input.contains("how many")){questiontype = "checkfor";input = input.replace("do you have", "<b>do you have</b>");}
		if (input.contains("i look for")){questiontype = "checkfor"; input = input.replace("i look for", "<b>i look for</b>");}
		if (input.contains("is there")){questiontype = "checkfor"; input = input.replace("is there", "<b>is there</b>");}

		if (input.contains("describe") 
				|| input.contains("description")
				|| input.contains("what is"))

		{
			questiontype = "description";
			System.out.println("Find Description");
		}
		
		if (input.contains("life") 
				|| input.contains("battery"))

		{
			questiontype = "battery";
			System.out.println("Battery life");
		}
		
		if (input.contains(" os") 
				|| input.contains("operating")
				|| input.contains("system")
				|| input.contains("operatingsystem"))

		{
			questiontype = "os";
			System.out.println("os");
		}
	
		if (input.contains("capacity") 
				|| input.contains("size")
				|| input.contains("storage"))

		{
			questiontype = "capacity";
			System.out.println("capacity");
		}
		
		if (input.contains("colour") 
				|| input.contains("colour")
				|| input.contains("color"))

		{
			questiontype = "colour";
			System.out.println("colour");
		}
		
		if (input.contains("camerapixels") 
				|| input.contains("camera")
				|| input.contains("pixels")
				|| input.contains("photo"))

		{
			questiontype = "camerapixels";
			System.out.println("camerapixels");
		}
		
		if (input.contains("cost") 
				|| input.contains("price")
				|| input.contains("how much"))

		{
			questiontype = "cost";
			System.out.println("cost");
		}
		
		if (input.contains("can i buy") 
				|| input.contains("can i have")
				|| input.contains("can i get the phone")
				|| input.contains("am i able to")
				|| input.contains("could i buy") 
				|| input.contains("i want to buy")
				|| input.contains("i want the"))

		{
			questiontype = "intent";
			System.out.println("Find Phone Availability");
		}
		
		if (input.contains("cake")
				|| input.contains("HL3"))
		{
			questiontype = "cake";
			
		}
		if (input.contains("help")
				|| input.contains("halp")
				|| input.contains("confused")
				|| input.contains("confuse")
				|| input.contains("dunno")
				|| input.contains("don't know"))
		{
			questiontype = "help";
			
		}
		if (input.contains("thank you") 
				|| input.contains("bye")
				|| input.contains("thanks")
				|| input.contains("cool thank")
				|| input.contains("k thx bye")
				|| input.contains("Domo Arigato")
				|| input.contains("Danke schon")) 			

		{
			questiontype = "farewell";
		}


		// ------- Checking the Subject of the Question --------------------------------------
		boolean foundSubject=false;
		if(foundSubject==false){
		for (int x = 0; x < phonesyn.size(); x++) {    
			if (input.contains(phonesyn.get(x))) {     
				classtype = thePhoneList;              
				foundSubject=true;
				input = input.replace(phonesyn.get(x), "<b>"+phonesyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Phone recognised.");
			
			}
		}}
		
		if(foundSubject==false){
			for (int x = 0; x < reviewsyn.size(); x++) {    
				if (input.contains(reviewsyn.get(x))) {     
					classtype = theReviewList;              
					foundSubject=true;
					input = input.replace(reviewsyn.get(x), "<b>"+reviewsyn.get(x)+"</b>");
					
					subjectcounter = 1;
					System.out.println("Class type review recognised.");
				
				}
			}}
		
		if(foundSubject==false){
			for (int x = 0; x < customersyn.size(); x++) {   
				if (input.contains(customersyn.get(x))) {    
					classtype = theCustomerList;             
					foundSubject=true;
					input = input.replace(customersyn.get(x), "<b>"+customersyn.get(x)+"</b>");
					
					subjectcounter = 1;
					System.out.println("Class type Customer recognised.");
					
				}
		}}
		
		if(foundSubject==false){
		for (int x = 0; x < stocksyn.size(); x++) {   
			if (input.contains(stocksyn.get(x))) {    
				classtype = theStockList;             
				foundSubject=true;
				input = input.replace(stocksyn.get(x), "<b>"+stocksyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Stock recognised.");
				
			}
		}}
		
		if(foundSubject==false){
		for (int x = 0; x < tabletsyn.size(); x++) {    
			if (input.contains(tabletsyn.get(x))) {     
				classtype = theTabletList;              
				foundSubject=true;
				input = input.replace(tabletsyn.get(x), "<b>"+tabletsyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Tablet recognised.");
				
			}
		}}
		
		if(subjectcounter == 0){
			for (int x = 0; x < recentobjectsyn.size(); x++) {  
				if (input.contains(recentobjectsyn.get(x))) {
					classtype = theRecentThing;
					
					input = input.replace(recentobjectsyn.get(x), "<b>"+recentobjectsyn.get(x)+"</b>");
					
					subjectcounter = 1;
					System.out.println("Class type recognised as"+recentobjectsyn.get(x));
				}
			}
		}

		// More than one subject in question + PhoneLibrary ...
	

		System.out.println("subjectcounter = "+subjectcounter);

		for (int x = 0; x < phonelibrarysyn.size(); x++) {   

			if (input.contains(phonelibrarysyn.get(x))) {    
				if (subjectcounter == 0) { 
					
					input = input.replace(phonelibrarysyn.get(x), "<b>"+phonelibrarysyn.get(x)+"</b>");
					classtype = thePhoneLibraryList;         

					System.out.println("class type PhoneLibrary recognised");		

				}
			}
		}

		// Compose Method call and generate answerVector

		if (questiontype == "amount") { // Number of Subject

			Integer numberof = Count(classtype);

			answer=("The number of "
					+ classtype.get(0).getClass().getSimpleName() + "s is "
					+ numberof + ".");

			Answered = 1; // An answer was given

		}

		if (questiontype == "list") { // List all Subjects of a kind

			answer=("You asked for the listing of all "
					+ classtype.get(0).getClass().getSimpleName() + "s. <br>"
					+ "We have the following "
					+ classtype.get(0).getClass().getSimpleName() + "s:"
					+ ListAll(classtype));
			Answered = 1; // An answer was given

		}

		if (questiontype == "checkfor") { // test for a certain Subject instance

			Vector<String> check = CheckFor(classtype, input);
			answer=(check.get(0));
			Answered = 1; // An answer was given
			if (check.size() > 1) {
				Currentitemofinterest = classtype.get(Integer.valueOf(check
						.get(1)));
				System.out.println("Classtype List = "
						+ classtype.getClass().getSimpleName());
				System.out.println("Index in List = "
						+ Integer.valueOf(check.get(1)));
				Currentindex = Integer.valueOf(check.get(1));
				theRecentThing.clear(); // Clear it before adding (changing) the
				// now recent thing
				theRecentThing.add(classtype.get(Currentindex));
			}
		}

		// Description Question in Pronomial form "Where can i find it"

		if (questiontype == "description") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("The description of  "
					+ classtype.get(0).getClass().getSimpleName() + " " + "is "
					+ Description(classtype, input));
			Answered = 1; // An answer was given
		}	
		if (questiontype == "cake") {  

			answer=("The cake is a lie");

			Answered = 1; // An answer was given
		}
		if (questiontype == "help") {  

			answer=("Please type in question such as 'show all phones' OR 'show all tablets' OR reviews");

			Answered = 1; // An answer was given
		}	
		

		if (questiontype == "battery") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("The battery life of  "
					+ classtype.get(0).getClass().getSimpleName() + " " + "is "
					+ Battery(classtype, input));

			Answered = 1; // An answer was given
		}	
		
		if (questiontype == "os") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("The os of  "
					+ classtype.get(0).getClass().getSimpleName() + " " + "is "
					+ Os(classtype, input));

			Answered = 1; // An answer was given
		}	
		
		if (questiontype == "capacity") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("The capacity of  "
					+ classtype.get(0).getClass().getSimpleName() + " " + "is "
					+ Capacity(classtype, input));

			Answered = 1; // An answer was given
		}	
		
		if (questiontype == "colour") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("The colour of  "
					+ classtype.get(0).getClass().getSimpleName() + " " + "is "
					+ Colour(classtype, input));

			Answered = 1; // An answer was given
		}	
		
		if (questiontype == "camerapixels") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("The camerapixels of  "
					+ classtype.get(0).getClass().getSimpleName() + " " + "is "
					+ Camerapixels(classtype, input));

			Answered = 1; // An answer was given
		}	
		
		if (questiontype == "cost") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("The cost of  "
					+ classtype.get(0).getClass().getSimpleName() + " " + "is "
					+ Cost(classtype, input));

			Answered = 1; // An answer was given
		}	
		

		if ((questiontype == "intent" && classtype == thePhoneList) 
				||(questiontype == "intent" && classtype == theRecentThing)) {

			// is the phone in stock or not
			answer=(PhoneAvailable(classtype, input));
			Answered = 1; // An answer was given
		}

		if ((questiontype == "intent" && classtype == theTabletList) 
				||(questiontype == "intent" && classtype == theRecentThing)) {

			// is the tablet in stock or not
			answer=(TabletAvailable(classtype, input));
			Answered = 1; // An answer was given
		}		
		
		if (questiontype == "farewell") {       // Reply to a farewell
			
			answer=("You are welcome.");
			
			Answered = 1; // An answer was given
		}
		
		
		if (Answered == 0) { // No answer was given		
			if (error >= 3)
			{
				error= 0;
				answer=("NAUGHTY NAUGHTY YOU SHOULD REALLY TRY TO LEARN TO TYPE 'HELP'");
			}
			else
			{
			error+=1;
			answer=("Sorry I didn't understand the term" +" '"+ input +"'.");
			
			}
		}

		out.add(input);
		out.add(answer);
		
		return out;
	}

	// Methods to generate answers for the different kinds of Questions
	
	// Answer a question of the "Is a phone or "it (meaning a phone) available ?" kind

	public String PhoneAvailable(List thelist, String input) {

		boolean available =false;
		String answer ="";
		Phone curphone = new Phone();
		String phonename="";

		if (thelist == thePhoneList) {                       

			int counter = 0;

			//Identify which phone is asked for 

			for (int i = 0; i < thelist.size(); i++) {

				curphone = (Phone) thelist.get(i);          

				if (input.contains(curphone.getName().toLowerCase())             
						|| input.contains(curphone.getBrand().toLowerCase())       
						|| input.contains(curphone.getPid().toLowerCase())) {   

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); 									//Clear it before adding (changing) the
					classtype = thePhoneList;                                     
					theRecentThing.add(classtype.get(Currentindex));
					phonename=curphone.getName();
										
					if (input.contains(curphone.getName().toLowerCase())){input = input.replace(curphone.getName().toLowerCase(), "<b>"+curphone.getName().toLowerCase()+"</b>");}          
					if (input.contains(curphone.getBrand().toLowerCase())) {input = input.replace(curphone.getBrand().toLowerCase(), "<b>"+curphone.getBrand().toLowerCase()+"</b>");}     
					if (input.contains(curphone.getPid().toLowerCase())){input = input.replace(curphone.getPid().toLowerCase(), "<b>"+curphone.getPid().toLowerCase()+"</b>");}
										
					i = thelist.size() + 1; 									// force break
				}
			}
		}

		// maybe other way round or double 

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				curphone = (Phone) theRecentThing.get(0);                		
				phonename=curphone.getName();
			}
		}

		// check all stocks if they contain the phones

		for (int i = 0; i < theStockList.size(); i++) {

			Stock curstock = (Stock) theStockList.get(i);          

			// If there is a stock with the phones, the phone is not available

			if ( curphone.getBrand().toLowerCase().equals(curstock.getBrand().toLowerCase())) {            

				input = input.replace(curstock.getBrand().toLowerCase(), "<b>"+curstock.getBrand().toLowerCase()+"</b>");
				
				available=true;
				i = thelist.size() + 1; 									// force break
			}
		}

		if(available){
			answer="We have that product in stock.";
		}
		else{ 
			answer="We do not have that product in stock.";
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ phonename;
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return(answer);

	}
	
	public String TabletAvailable(List thelist, String input) {

		boolean available =false;
		String answer ="";
		Tablet curtablet = new Tablet();
		String tabletname="";

		if (thelist == theTabletList) {                       

			int counter = 0;

			//Identify which phone is asked for 

			for (int i = 0; i < thelist.size(); i++) {

				curtablet = (Tablet) thelist.get(i);          

				if (input.contains(curtablet.getName().toLowerCase())             
						|| input.contains(curtablet.getBrand().toLowerCase())       
						|| input.contains(curtablet.getPid().toLowerCase())) {   

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); 									//Clear it before adding (changing) the
					classtype = theTabletList;                                     
					theRecentThing.add(classtype.get(Currentindex));
					tabletname=curtablet.getName();
										
					if (input.contains(curtablet.getName().toLowerCase())){input = input.replace(curtablet.getName().toLowerCase(), "<b>"+curtablet.getName().toLowerCase()+"</b>");}          
					if (input.contains(curtablet.getBrand().toLowerCase())) {input = input.replace(curtablet.getBrand().toLowerCase(), "<b>"+curtablet.getBrand().toLowerCase()+"</b>");}     
					if (input.contains(curtablet.getPid().toLowerCase())){input = input.replace(curtablet.getPid().toLowerCase(), "<b>"+curtablet.getPid().toLowerCase()+"</b>");}
										
					i = thelist.size() + 1; 									// force break
				}
			}
		}

		// maybe other way round or double 

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				curtablet = (Tablet) theRecentThing.get(0);                		
				tabletname=curtablet.getName();
			}
		}

		// check all stocks if they contain the phones

		for (int i = 0; i < theStockList.size(); i++) {

			Stock curstock = (Stock) theStockList.get(i);          

			// If there is a stock with the phones, the phone is not available

			if ( curtablet.getBrand().toLowerCase().equals(curstock.getBrand().toLowerCase())) {            

				input = input.replace(curstock.getBrand().toLowerCase(), "<b>"+curstock.getBrand().toLowerCase()+"</b>");
				
				available=true;
				i = thelist.size() + 1; 									// force break
			}
		}

		if(available){
			answer="We have that product in stock.";
		}
		else{ 
			answer="We do not have that product in stock.";
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ tabletname;
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return(answer);

	}

	// Answer a question of the "How many ...." kind 
	
	public Integer Count(List thelist) { // List "thelist": List of Class Instances (e.g. thePhoneList)

		//URL = "http://en.wiktionary.org/wiki/"		

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return thelist.size();
	}

	// Answer a question of the "What kind of..." kind
	
	public String ListAll(List thelist) {

		String listemall = "<ul>";

		if (thelist == thePhoneList) {                                   
			for (int i = 0; i < thelist.size(); i++) {
				Phone curphone = (Phone) thelist.get(i);                   
				listemall = listemall + "<li>" + (curphone.getName() + "</li>");     
			}
		}
		if (thelist == theReviewList) {                                   
			for (int i = 0; i < thelist.size(); i++) {
				Review currev = (Review) thelist.get(i);                   
				listemall = listemall + "<li>" + (currev.getPid()+" "+currev.getPost() + "</li>");     
			}
		}

		if (thelist == theCustomerList) {                                 
			for (int i = 0; i < thelist.size(); i++) {
				Customer curmem = (Customer) thelist.get(i);                
				listemall = listemall + "<li>"                          
						+ (curmem.getFirstname() + " " + curmem.getLastname() + "</li>");   
			}
		}


		
		if (thelist == theStockList) {                                
			for (int i = 0; i < thelist.size(); i++) {
				Stock curstock = (Stock) thelist.get(i);              
				listemall = listemall + "<li>" 
						+ (curstock.getBrand() + "</li>");                 
			}
		}
		
		if (thelist == theTabletList) {                                   
			for (int i = 0; i < thelist.size(); i++) {
				Tablet curtablet = (Tablet) thelist.get(i);                   
				listemall = listemall + "<li>" + (curtablet.getName() + "</li>");     
			}
		}
		
		listemall += "</ul>";

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);
		
		return listemall;
	}

	// Answer a question of the "Do you have..." kind 
	
	public Vector<String> CheckFor(List thelist, String input) {

		Vector<String> yesorno = new Vector<String>();
		if (classtype.isEmpty()){
			yesorno.add("Class not recognised. Please specify if you are searching for a phone or customer?");
		} else {
			yesorno.add("No we don't have such a "
				+ classtype.get(0).getClass().getSimpleName());
		}

		Integer counter = 0;

		if (thelist == thePhoneList) {                          

			for (int i = 0; i < thelist.size(); i++) {

				Phone curphone = (Phone) thelist.get(i);                            

				if (input.contains(curphone.getName().toLowerCase())             
						|| input.contains(curphone.getBrand().toLowerCase())       
						|| input.contains(curphone.getPid().toLowerCase())) {   

					counter = i;
					String message="Yes we have such a Phone:\n"+curphone.getName()+" from "+
					curphone.getBrand()+" with: battery life-"+curphone.getBattery()+
					"and capacity of "+curphone.getCapacity()+"and camera pixels-"+curphone.getCamerapixels()+
					" and the colour "+curphone.getColour()+ " and the cost of "+curphone.getCost()+
					" and the OS-"+curphone.getOs();
					
					boolean gotrev=false;
					for (int j=0;j<theReviewList.size();j++)
					{
						Review theRev=(Review)theReviewList.get(j);
						if (curphone.getPid().toLowerCase().equals(theRev.getPid().toLowerCase())){
							message =message+ "<br/>Review no."+theRev.getReviewid()+": "+theRev.getPost()+"\n";
							gotrev=true;
							}
					}
					if (gotrev==false) message+= "<br/>No reviews yet";
					yesorno.set(0, message);  
					yesorno.add(counter.toString());
					i = thelist.size() + 1; // force break
				}
			}
		}

		if (thelist == theCustomerList) {                                       
			for (int i = 0; i < thelist.size(); i++) {
				Customer curmem = (Customer) thelist.get(i);                       
				if (input.contains(curmem.getFirstname().toLowerCase())          
						|| input.contains(curmem.getLastname().toLowerCase())  
						|| input.contains(curmem.getCity().toLowerCase())) {   

					counter = i;
					yesorno.set(0, "Yes we have such a Customer");                
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}


		
		if (thelist == theStockList) {                                      
			yesorno.set(0, "Access denied");
		}
		
		if (thelist == theTabletList) {                          

			for (int i = 0; i < thelist.size(); i++) {

				Tablet curtablet = (Tablet) thelist.get(i);                            

				if (input.contains(curtablet.getName().toLowerCase())             
						|| input.contains(curtablet.getBrand().toLowerCase())       
						|| input.contains(curtablet.getPid().toLowerCase())) {   
					
					String message="Yes we have such a Phone:\n"+curtablet.getName()+" from "+
							curtablet.getBrand()+" with: battery life-"+curtablet.getBattery()+
							"and capacity of "+curtablet.getCapacity()+"and camera pixels-"+curtablet.getCamerapixels()+
							" and the colour "+curtablet.getColour()+ " and the cost of "+curtablet.getCost()+
							" and the OS-"+curtablet.getOs();
					boolean gotrev=false;
					for (int j=0;j<theReviewList.size();j++)
					{
						Review theRev=(Review)theReviewList.get(j);
						if (curtablet.getPid().toLowerCase().equals(theRev.getPid().toLowerCase())){
							message =message+ "<br/>Review no."+theRev.getReviewid()+": "+theRev.getPost()+"\n";
							gotrev=true;
							}
					}
					if (gotrev==false) message+= "<br/>No reviews yet";
					yesorno.set(0, message);  
					counter = i;                 
					yesorno.add(counter.toString());
					i = thelist.size() + 1; // force break
				}
			}
		}

		if (classtype.isEmpty()) {
			System.out.println("Not class type given.");
		} else {
			URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			System.out.println("URL = "+URL);
			tooltipstring = readwebsite(URL);
			String html = "<html>" + tooltipstring + "</html>";
			Myface.setmytooltip(html);
			Myface.setmyinfobox(URL2);
		}
	
		return yesorno;
	}

	//  Method to retrieve the battery information from the object (life) kind

	public String Battery(List classtypelist, String input) {

		List thelist = classtypelist;
		String battery = "that does not have a battery";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				Phone curphone = (Phone) theRecentThing.get(0);           
				battery = (curphone.getBattery() + " ");              

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				Tablet curtablet = (Tablet) theRecentThing.get(0);           
				battery = (curtablet.getBattery() + " ");              

			}

		

		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);          

					if (input.contains(curphone.getName().toLowerCase())             
							|| input.contains(curphone.getBrand().toLowerCase())       
							|| input.contains(curphone.getPid().toLowerCase())) {   

						counter = i;
						battery = (curphone.getBattery() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);          

					if (input.contains(curtablet.getName().toLowerCase())             
							|| input.contains(curtablet.getBrand().toLowerCase())       
							|| input.contains(curtablet.getPid().toLowerCase())) {   

						counter = i;
						battery = (curtablet.getBattery() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
	
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return battery;
	}	

	public String Os(List classtypelist, String input) {

		List thelist = classtypelist;
		String os = "that does not have an Operating system";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				Phone curphone = (Phone) theRecentThing.get(0);           
				os = (curphone.getOs() + " ");              

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				Tablet curtablet = (Tablet) theRecentThing.get(0);           
				os = (curtablet.getOs() + " ");              

			}



		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);          

					if (input.contains(curphone.getName().toLowerCase())             
							|| input.contains(curphone.getBrand().toLowerCase())       
							|| input.contains(curphone.getPid().toLowerCase())) {   

						counter = i;
						os = (curphone.getOs() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);          

					if (input.contains(curtablet.getName().toLowerCase())             
							|| input.contains(curtablet.getBrand().toLowerCase())       
							|| input.contains(curtablet.getPid().toLowerCase())) {   

						counter = i;
						os = (curtablet.getOs() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			

		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return os;
	}
	
	public String Capacity(List classtypelist, String input) {

		List thelist = classtypelist;
		String capacity = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				Phone curphone = (Phone) theRecentThing.get(0);           
				capacity = (curphone.getCapacity() + " ");              

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				Tablet curtablet = (Tablet) theRecentThing.get(0);           
				capacity = (curtablet.getCapacity() + " ");              

			}



		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);          

					if (input.contains(curphone.getName().toLowerCase())             
							|| input.contains(curphone.getBrand().toLowerCase())       
							|| input.contains(curphone.getPid().toLowerCase())) {   

						counter = i;
						capacity = (curphone.getCapacity() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);          

					if (input.contains(curtablet.getName().toLowerCase())             
							|| input.contains(curtablet.getBrand().toLowerCase())       
							|| input.contains(curtablet.getPid().toLowerCase())) {   

						counter = i;
						capacity = (curtablet.getCapacity() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			


		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return capacity;
	}	
	
	public String Colour(List classtypelist, String input) {

		List thelist = classtypelist;
		String colour = "That does not have a colour";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				Phone curphone = (Phone) theRecentThing.get(0);           
				colour = (curphone.getColour() + " ");              

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				Tablet curtablet = (Tablet) theRecentThing.get(0);           
				colour = (curtablet.getColour() + " ");              

			}

	


		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);          

					if (input.contains(curphone.getName().toLowerCase())             
							|| input.contains(curphone.getBrand().toLowerCase())       
							|| input.contains(curphone.getPid().toLowerCase())) {   

						counter = i;
						colour = (curphone.getColour() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);          

					if (input.contains(curtablet.getName().toLowerCase())             
							|| input.contains(curtablet.getBrand().toLowerCase())       
							|| input.contains(curtablet.getPid().toLowerCase())) {   

						counter = i;
						colour = (curtablet.getColour() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return colour;
	}	
	
	public String Camerapixels(List classtypelist, String input) {

		List thelist = classtypelist;
		String camerapixels = "That does not have pixels";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				Phone curphone = (Phone) theRecentThing.get(0);           
				camerapixels = (curphone.getCamerapixels() + " ");              

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				Tablet curtablet = (Tablet) theRecentThing.get(0);           
				camerapixels = (curtablet.getCamerapixels() + " ");              

			}


		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);          

					if (input.contains(curphone.getName().toLowerCase())             
							|| input.contains(curphone.getBrand().toLowerCase())       
							|| input.contains(curphone.getPid().toLowerCase())) {   

						counter = i;
						camerapixels = (curphone.getCamerapixels() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);          

					if (input.contains(curtablet.getName().toLowerCase())             
							|| input.contains(curtablet.getBrand().toLowerCase())       
							|| input.contains(curtablet.getPid().toLowerCase())) {   

						counter = i;
						camerapixels = (curtablet.getCamerapixels() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return camerapixels;
	}	
	
	public String Cost(List classtypelist, String input) {

		List thelist = classtypelist;
		String cost = "That does not have a cost";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				Phone curphone = (Phone) theRecentThing.get(0);           
				cost = (curphone.getCost() + " ");              

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				Tablet curtablet = (Tablet) theRecentThing.get(0);           
				cost = (curtablet.getCost() + " ");              

			}

		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);          

					if (input.contains(curphone.getName().toLowerCase())             
							|| input.contains(curphone.getBrand().toLowerCase())       
							|| input.contains(curphone.getPid().toLowerCase())) {   

						counter = i;
						cost = (curphone.getCost() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);          

					if (input.contains(curtablet.getName().toLowerCase())             
							|| input.contains(curtablet.getBrand().toLowerCase())       
							|| input.contains(curtablet.getPid().toLowerCase())) {   

						counter = i;
						cost = (curtablet.getCost() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			

		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return cost;
	}	
	
	public String Description(List classtypelist, String input) {

		List thelist = classtypelist;
		String description = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                   

				Phone curphone = (Phone) theRecentThing.get(0);           
				description = (curphone.getDescription() + " ");              

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                   

				Tablet curtablet = (Tablet) theRecentThing.get(0);           
				description = (curtablet.getDescription() + " ");              

			}			
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("customer")) {                

				       
				description = "a ordinary customer";                                     

			}



			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("phonelibrary")) {                   

				description = "the main phone store";                                            
			}

		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);          

					if (input.contains(curphone.getName().toLowerCase())             
							|| input.contains(curphone.getBrand().toLowerCase())       
							|| input.contains(curphone.getPid().toLowerCase())) {   

						counter = i;
						description = (curphone.getDescription() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);          

					if (input.contains(curtablet.getName().toLowerCase())             
							|| input.contains(curtablet.getBrand().toLowerCase())       
							|| input.contains(curtablet.getPid().toLowerCase())) {   

						counter = i;
						description = (curtablet.getDescription() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                     
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

			if (thelist == theCustomerList) {                                          

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Customer curcustomer = (Customer) thelist.get(i);         				   

					if (input.contains(curcustomer.getFirstname().toLowerCase())               
							|| input.contains(curcustomer.getLastname().toLowerCase())       
							|| input.contains(curcustomer.getCustomerid().toLowerCase())) {    

						counter = i;
						description = (curcustomer.getCity() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 										// Clear it before adding (changing) the
						classtype = theCustomerList;            	 						 
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 				             	        // force break
					}
				}
			}

		

			if (thelist == thePhoneLibraryList) {                                                   

				description = "the main phone store";                                                      
			}
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return description;
	}

	
	public String testit() {   // test the loaded knowledge by querying for phones written by dostoyjewski

		String answer = "";

		System.out.println("Phone List = " + thePhoneList.size());   

		for (int i = 0; i < thePhoneList.size(); i++) {   // check each phone in the List,  

			Phone curphone = (Phone) thePhoneList.get(i);    // cast list element to Phone Class  												
			System.out.println("Testing Phone" + " " + curphone.getPid());

			if (curphone.getPid().equalsIgnoreCase("001")) {     // check for the author  

				answer = "Phone by : " + curphone.getBrand() + "\n"   
						+ " Phone Model: " + curphone.getName()       
						+ ".";
			}
		}
		return answer;
	}

	public String readwebsite(String url) {

		String webtext = "";
		try {
			BufferedReader readit = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));

			String lineread = readit.readLine();

			System.out.println("Reader okay");

			while (lineread != null) {
				webtext = webtext + lineread;
				lineread = readit.readLine();				
			}

			// Hard coded cut out from "wordnet website source text": 
			//Check if website still has this structure   vvvv ...definitions...  vvvv 		
			
			webtext = webtext.substring(webtext.indexOf("<ul>"),webtext.indexOf("</ul>"));                                 //               ^^^^^^^^^^^^^^^^^              

			webtext = "<table width=\"700\"><tr><td>" + webtext
					+ "</ul></td></tr></table>";

		} catch (Exception e) {
			webtext = "Not yet";
			System.out.println("Error connecting to wordnet");
		}
		return webtext;
	}
}
