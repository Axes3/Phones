package Examples;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import Examples.PhoneLibrary;
import Examples.Phone;
import Examples.Customer;
import Examples.Catalog;
import Examples.Stock;

import Examples.SimpleGUI;

public class Reasoner {

	// The main Class Object holding the Domain knowledge

	// Generate the classes automatically with: Opening a command console and
	// type:
	// Path to YOUR-PROJECTROOT-IN-WORKSPACE\xjc.bat yourschemaname.xsd -d src
	// -p yourclasspackagename

	public PhoneLibrary thephonelibrary; //This is a candidate for a name change

	public SimpleGUI Myface;

	// The lists holding the class instances of all domain entities

	public List thePhoneLibraryList = new ArrayList(); //This is a candidate for a name change
	public List thePhoneList = new ArrayList();    //This is a candidate for a name change
	public List theCustomerList = new ArrayList();  //This is a candidate for a name change
	public List theCatalogList = new ArrayList(); //This is a candidate for a name change
	public List theStockList = new ArrayList(); //This is a candidate for a name change
	public List theRecentThing = new ArrayList();
	public List theTabletList = new ArrayList();    //This is a candidate for a name change
	public static int error = 0;
	// Gazetteers to store synonyms for the domain entities names

	public Vector<String> phonelibrarysyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> phonesyn = new Vector<String>();     //This is a candidate for a name change
	public Vector<String> customersyn = new Vector<String>();   //This is a candidate for a name change
	public Vector<String> catalogsyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> stocksyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> recentobjectsyn = new Vector<String>();
	public Vector<String> tabletsyn = new Vector<String>();     //This is a candidate for a name change

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

		//This is a candidate for a name change
		File xmlfiletoload = new File("PhoneLibrary.xml"); // we need a (CURRENT)  file (xml) to load  

		// Init synonmys and typo forms in gazetteers

		phonelibrarysyn.add("things");   	//This is a candidate for a name change
		phonelibrarysyn.add("place");		//This is a candidate for a name change
		phonelibrarysyn.add("phonestore");	//This is a candidate for a name change
		phonelibrarysyn.add("phonehouse"); 	//This is a candidate for a name change
		phonelibrarysyn.add("store");		//This is a candidate for a name change
		phonelibrarysyn.add("house");		//This is a candidate for a name change
		phonelibrarysyn.add("offer");		//This is a candidate for a name change
		phonelibrarysyn.add("store");		//This is a candidate for a name change

		phonesyn.add(" phone");    //All of the following is a candidate for a name change
		phonesyn.add(" pone");
		phonesyn.add(" phone");
		phonesyn.add("celular");
		phonesyn.add("talker");
		phonesyn.add(" smartphone");
		phonesyn.add(" brick");
		phonesyn.add(" smarthpone");
		
		tabletsyn.add(" tablet");    //All of the following is a candidate for a name change
		tabletsyn.add(" tab");
		tabletsyn.add(" tabblet");


		customersyn.add("customer"); //All of the following is a candidate for a name change
		customersyn.add("custome");
		customersyn.add("client");
		customersyn.add("dude");
		customersyn.add("man");
		customersyn.add("guy");

		catalogsyn.add("catalog");  //All of the following is a candidate for a name change
		catalogsyn.add("phonelist");
		catalogsyn.add("inventory");

		stocksyn.add(" stock");   //All of the following is a candidate for a name change

		recentobjectsyn.add(" this");   //All of the following is a candidate for a name change
		recentobjectsyn.add(" that");
		recentobjectsyn.add(" him");
		recentobjectsyn.add(" her");	// spaces to prevent collision with "wHERe"	
		recentobjectsyn.add(" it");

		try {
			FileInputStream readthatfile = new FileInputStream(xmlfiletoload); // initiate input stream

			thephonelibrary = xmlhandler.loadXML(readthatfile);

			// Fill the Lists with the objects data just generated from the xml

			thePhoneList = thephonelibrary.getPhone();  		//This is a candidate for a name change
			theCustomerList = thephonelibrary.getCustomer(); 	//This is a candidate for a name change
			theCatalogList = thephonelibrary.getCatalog(); 	//This is a candidate for a name change
			theStockList = thephonelibrary.getStock(); 	//This is a candidate for a name change
			theTabletList = thephonelibrary.getTablet();  		//This is a candidate for a name change
			thePhoneLibraryList.add(thephonelibrary);             // force it to be a List, //This is a candidate for a name change

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
		
		// Answer Generation Idea: content = Questiontype-method(classtype class) (+optional attribute)

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

		if (input.contains("is there a")){questiontype = "checkfor"; input = input.replace("is there a", "<b>is there a</b>");}
		if (input.contains("i am searching")){questiontype = "checkfor"; input = input.replace("i am searching", "<b>i am searching</b>");}
		if (input.contains("i am looking for")){questiontype = "checkfor"; input = input.replace("i am looking for", "<b>i am looking for</b>");}
		if (input.contains("do you have")&&!input.contains("how many")){questiontype = "checkfor";input = input.replace("do you have", "<b>do you have</b>");}
		if (input.contains("i look for")){questiontype = "checkfor"; input = input.replace("i look for", "<b>i look for</b>");}
		if (input.contains("is there")){questiontype = "checkfor"; input = input.replace("is there", "<b>is there</b>");}

		if (input.contains("where") 
				|| input.contains("can't find")
				|| input.contains("can i find") 
				|| input.contains("way to"))

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
		
		
		if (input.contains("can i stock") 
				|| input.contains("can i borrow")
				|| input.contains("can i get the phone")
				|| input.contains("am i able to")
				|| input.contains("could i stock") 
				|| input.contains("i want to stock")
				|| input.contains("i want to borrow"))

		{
			questiontype = "intent";
			System.out.println("Find Phone Availability");
		}
		
		if (input.contains("cake") )
				

		{
			questiontype = "cake";
			
		}
		
		if (input.contains("thank you") 
				|| input.contains("bye")
				|| input.contains("thanks")
				|| input.contains("cool thank")
				|| input.contains("Domo Arigato")
				|| input.contains("Danke schon")) 			

		{
			questiontype = "farewell";
		}


		// ------- Checking the Subject of the Question --------------------------------------

		for (int x = 0; x < phonesyn.size(); x++) {   //This is a candidate for a name change
			if (input.contains(phonesyn.get(x))) {    //This is a candidate for a name change
				classtype = thePhoneList;             //This is a candidate for a name change
				
				input = input.replace(phonesyn.get(x), "<b>"+phonesyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Phone recognised.");
			}
		}
		for (int x = 0; x < customersyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(customersyn.get(x))) {   //This is a candidate for a name change
				classtype = theCustomerList;            //This is a candidate for a name change
				
				input = input.replace(customersyn.get(x), "<b>"+customersyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Customer recognised.");
			}
		}
		for (int x = 0; x < catalogsyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(catalogsyn.get(x))) {   //This is a candidate for a name change
				classtype = theCatalogList;            //This is a candidate for a name change
				
				input = input.replace(catalogsyn.get(x), "<b>"+catalogsyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Catalog recognised.");
			}
		}
		for (int x = 0; x < stocksyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(stocksyn.get(x))) {   //This is a candidate for a name change
				classtype = theStockList;            //This is a candidate for a name change
				
				input = input.replace(stocksyn.get(x), "<b>"+stocksyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Stock recognised.");
			}
		}
		for (int x = 0; x < tabletsyn.size(); x++) {   //This is a candidate for a name change
			if (input.contains(tabletsyn.get(x))) {    //This is a candidate for a name change
				classtype = theTabletList;             //This is a candidate for a name change
				
				input = input.replace(tabletsyn.get(x), "<b>"+tabletsyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Tablet recognised.");
			}
		}
		
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
		// "Does the PhoneLibrary has .. Subject 2 ?"

		System.out.println("subjectcounter = "+subjectcounter);

		for (int x = 0; x < phonelibrarysyn.size(); x++) {  //This is a candidate for a name change

			if (input.contains(phonelibrarysyn.get(x))) {   //This is a candidate for a name change

				// Problem: "How many Phones does the PhoneLibrary have ?" -> classtype = PhoneLibrary
				// Solution:
				
				if (subjectcounter == 0) { // PhoneLibrary is the first subject in the question
					
					input = input.replace(phonelibrarysyn.get(x), "<b>"+phonelibrarysyn.get(x)+"</b>");
					
					classtype = thePhoneLibraryList;        //This is a candidate for a name change

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
				System.out.println("Index in Liste = "
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

			answer=("You can find the "
					+ classtype.get(0).getClass().getSimpleName() + " " + "at "
					+ Description(classtype, input));

			Answered = 1; // An answer was given
		}	
		if (questiontype == "cake") {  

			answer=("The cake is a lie");

			Answered = 1; // An answer was given
		}	
		// Description Question in Pronomial form "Where can i find it"

		if (questiontype == "battery") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("You can find the "
					+ classtype.get(0).getClass().getSimpleName() + " " + "at "
					+ Battery(classtype, input));

			Answered = 1; // An answer was given
		}	
		

		if ((questiontype == "intent" && classtype == thePhoneList) 
				||(questiontype == "intent" && classtype == theRecentThing)) {

			// Can I stock the phone or not (Can I lent "it" or not)
			answer=("You "+ PhoneAvailable(classtype, input));
			Answered = 1; // An answer was given
		}

		if ((questiontype == "intent" && classtype == theTabletList) 
				||(questiontype == "intent" && classtype == theRecentThing)) {

			// Can I stock the phone or not (Can I lent "it" or not)
			answer=("You "+ TabletAvailable(classtype, input));
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

		boolean available =true;
		String answer ="";
		Phone curphone = new Phone();
		String phonename="";

		if (thelist == thePhoneList) {                      //This is a candidate for a name change

			int counter = 0;

			//Identify which phone is asked for 

			for (int i = 0; i < thelist.size(); i++) {

				curphone = (Phone) thelist.get(i);         //This is a candidate for a name change

				if (input.contains(curphone.getName().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curphone.getBrand().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curphone.getPid().toLowerCase())) {  //This is a candidate for a name change

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); 									//Clear it before adding (changing) the
					classtype = thePhoneList;                                    //This is a candidate for a name change
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
					.toLowerCase().equals("phone")) {                  //This is a candidate for a name change

				curphone = (Phone) theRecentThing.get(0);               //This is a candidate for a name change		
				phonename=curphone.getName();
			}
		}

		// check all stocks if they contain the phones ISBN

		for (int i = 0; i < theStockList.size(); i++) {

			Stock curstock = (Stock) theStockList.get(i);         //This is a candidate for a name change

			// If there is a stock with the phones ISBN, the phone is not available

			if ( curphone.getBrand().toLowerCase().equals(curstock.getBrand().toLowerCase())) {           //This is a candidate for a name change

				input = input.replace(curstock.getBrand().toLowerCase(), "<b>"+curstock.getBrand().toLowerCase()+"</b>");
				
				available=false;
				i = thelist.size() + 1; 									// force break
			}
		}

		if(available){
			answer="can stock the phone.";
		}
		else{ 
			answer="cannot stock the phone as someone else has lent it at the moment.";
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

		boolean available =true;
		String answer ="";
		Tablet curtablet = new Tablet();
		String tabletname="";

		if (thelist == theTabletList) {                      //This is a candidate for a name change

			int counter = 0;

			//Identify which phone is asked for 

			for (int i = 0; i < thelist.size(); i++) {

				curtablet = (Tablet) thelist.get(i);         //This is a candidate for a name change

				if (input.contains(curtablet.getName().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curtablet.getBrand().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curtablet.getPid().toLowerCase())) {  //This is a candidate for a name change

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); 									//Clear it before adding (changing) the
					classtype = theTabletList;                                    //This is a candidate for a name change
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
					.toLowerCase().equals("tablet")) {                  //This is a candidate for a name change

				curtablet = (Tablet) theRecentThing.get(0);               //This is a candidate for a name change		
				tabletname=curtablet.getName();
			}
		}

		// check all stocks if they contain the phones ISBN

		for (int i = 0; i < theStockList.size(); i++) {

			Stock curstock = (Stock) theStockList.get(i);         //This is a candidate for a name change

			// If there is a stock with the phones ISBN, the phone is not available

			if ( curtablet.getBrand().toLowerCase().equals(curstock.getBrand().toLowerCase())) {           //This is a candidate for a name change

				input = input.replace(curstock.getBrand().toLowerCase(), "<b>"+curstock.getBrand().toLowerCase()+"</b>");
				
				available=false;
				i = thelist.size() + 1; 									// force break
			}
		}

		if(available){
			answer="can stock the tablet.";
		}
		else{ 
			answer="cannot stock the tablet as someone else has lent it at the moment.";
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

		if (thelist == thePhoneList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Phone curphone = (Phone) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + (curphone.getName() + "</li>");    //This is a candidate for a name change
			}
		}

		if (thelist == theCustomerList) {                                //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Customer curmem = (Customer) thelist.get(i);               //This is a candidate for a name change
				listemall = listemall + "<li>"                         //This is a candidate for a name change
						+ (curmem.getSurname() + " " + curmem.getLastname() + "</li>");  //This is a candidate for a name change
			}
		}

		if (thelist == theCatalogList) {                               //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Catalog curcat = (Catalog) thelist.get(i);             //This is a candidate for a name change
				listemall = listemall 
						+ "<li>" + (curcat.getName() + "</li>");      //This is a candidate for a name change
			}
		}
		
		if (thelist == theStockList) {                               //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Stock curstock = (Stock) thelist.get(i);             //This is a candidate for a name change
				listemall = listemall + "<li>" 
						+ (curstock.getBrand() + "</li>");                //This is a candidate for a name change
			}
		}
		
		if (thelist == theTabletList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Tablet curtablet = (Tablet) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + (curtablet.getName() + "</li>");    //This is a candidate for a name change
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
			yesorno.add("Class not recognised. Please specify if you are searching for a phone, catalog, customer, review, or stock?");
		} else {
			yesorno.add("No we don't have such a "
				+ classtype.get(0).getClass().getSimpleName());
		}

		Integer counter = 0;

		if (thelist == thePhoneList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Phone curphone = (Phone) thelist.get(i);                           //This is a candidate for a name change

				if (input.contains(curphone.getName().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curphone.getBrand().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curphone.getPid().toLowerCase())) {  //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Phone");                  //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1; // force break
				}
			}
		}

		if (thelist == theCustomerList) {                                      //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Customer curmem = (Customer) thelist.get(i);                      //This is a candidate for a name change
				if (input.contains(curmem.getSurname().toLowerCase())         //This is a candidate for a name change
						|| input.contains(curmem.getLastname().toLowerCase()) //This is a candidate for a name change
						|| input.contains(curmem.getCity().toLowerCase())) {  //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Customer");               //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}

		if (thelist == theCatalogList) {                                    //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Catalog curcat = (Catalog) thelist.get(i);                  //This is a candidate for a name change
				if (input.contains(curcat.getName().toLowerCase())          //This is a candidate for a name change
						|| input.contains(curcat.getUrl().toLowerCase())) { //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Catalog");           //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}
		
		if (thelist == theStockList) {                                     //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Stock curstock = (Stock) thelist.get(i);                  //This is a candidate for a name change
				if (input.contains(curstock.getBrand().toLowerCase())          //This is a candidate for a name change
					|| input.contains(curstock.getCustomerid().toLowerCase())){ //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Stock");            //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}
		
		if (thelist == theTabletList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Tablet curtablet = (Tablet) thelist.get(i);                           //This is a candidate for a name change

				if (input.contains(curtablet.getName().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curtablet.getBrand().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curtablet.getPid().toLowerCase())) {  //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Tablet");                  //This is a candidate for a name change
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

	//  Method to retrieve the description information from the object (Where is...) kind

	public String Battery(List classtypelist, String input) {

		List thelist = classtypelist;
		String battery = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                  //This is a candidate for a name change

				Phone curphone = (Phone) theRecentThing.get(0);          //This is a candidate for a name change
				battery = (curphone.getBattery() + " ");             //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("customer")) {               //This is a candidate for a name change

				Customer curmem = (Customer) theRecentThing.get(0);      //This is a candidate for a name change
				battery = (curmem.getCity() + " " + curmem.getStreet() + " " + curmem  //This is a candidate for a name change
						.getHousenumber());                                    //This is a candidate for a name change

			}


			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("phonelibrary")) {                  //This is a candidate for a name change

				battery = (thephonelibrary.getCity() + " " + thephonelibrary.getStreet() + thephonelibrary   //This is a candidate for a name change
						.getHousenumber());                                           //This is a candidate for a name change
			}

		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curphone.getName().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curphone.getBrand().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curphone.getPid().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						battery = (curphone.getBattery() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			

			if (thelist == theCustomerList) {                                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Customer curcustomer = (Customer) thelist.get(i);         				  //This is a candidate for a name change

					if (input.contains(curcustomer.getSurname().toLowerCase())              //This is a candidate for a name change
							|| input.contains(curcustomer.getLastname().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curcustomer.getCustomerid().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						battery = (curcustomer.getCity() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 										// Clear it before adding (changing) the
						classtype = theCustomerList;            	 						//This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 				             	        // force break
					}
				}
			}


			if (thelist == thePhoneLibraryList) {                                                  //This is a candidate for a name change

				battery = (thephonelibrary.getCity() + " " + thephonelibrary.getStreet() + thephonelibrary  //This is a candidate for a name change
						.getHousenumber());                                                   //This is a candidate for a name change
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
	
	public String Description(List classtypelist, String input) {

		List thelist = classtypelist;
		String description = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("phone")) {                  //This is a candidate for a name change

				Phone curphone = (Phone) theRecentThing.get(0);          //This is a candidate for a name change
				description = (curphone.getDescription() + " ");             //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("tablet")) {                  //This is a candidate for a name change

				Tablet curtablet = (Tablet) theRecentThing.get(0);          //This is a candidate for a name change
				description = (curtablet.getDescription() + " ");             //This is a candidate for a name change

			}			
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("customer")) {               //This is a candidate for a name change

				Customer curmem = (Customer) theRecentThing.get(0);      //This is a candidate for a name change
				description = (curmem.getCity() + " " + curmem.getStreet() + " " + curmem  //This is a candidate for a name change
						.getHousenumber());                                    //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()  
					.toLowerCase().equals("catalog")) {                 //This is a candidate for a name change

				Catalog curcat = (Catalog) theRecentThing.get(0);       //This is a candidate for a name change
				description = (curcat.getDescription() + " ");                //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("phonelibrary")) {                  //This is a candidate for a name change

				description = (thephonelibrary.getCity() + " " + thephonelibrary.getStreet() + thephonelibrary   //This is a candidate for a name change
						.getHousenumber());                                           //This is a candidate for a name change
			}

		}

		// if a direct noun was used (phone, customer, etc)

		else {

			if (thelist == thePhoneList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Phone curphone = (Phone) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curphone.getName().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curphone.getBrand().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curphone.getPid().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						description = (curphone.getDescription() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theTabletList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Tablet curtablet = (Tablet) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curtablet.getName().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curtablet.getBrand().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curtablet.getPid().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						description = (curtablet.getDescription() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theTabletList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

			if (thelist == theCustomerList) {                                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Customer curcustomer = (Customer) thelist.get(i);         				  //This is a candidate for a name change

					if (input.contains(curcustomer.getSurname().toLowerCase())              //This is a candidate for a name change
							|| input.contains(curcustomer.getLastname().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curcustomer.getCustomerid().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						description = (curcustomer.getCity() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 										// Clear it before adding (changing) the
						classtype = theCustomerList;            	 						//This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 				             	        // force break
					}
				}
			}

			if (thelist == theCatalogList) {                                       	 //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Catalog curcatalog = (Catalog) thelist.get(i);                    //This is a candidate for a name change

					if (input.contains(curcatalog.getName().toLowerCase())            //This is a candidate for a name change						     
							|| input.contains(curcatalog.getUrl().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						description = (curcatalog.getDescription() + " ");
						Currentindex = counter;
						theRecentThing.clear();                                      // Clear it before adding (changing) the	
						classtype = theCatalogList;                                  //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1;                                      // force break
					}
				}
			}

			if (thelist == thePhoneLibraryList) {                                                  //This is a candidate for a name change

				description = (thephonelibrary.getCity() + " " + thephonelibrary.getStreet() + thephonelibrary  //This is a candidate for a name change
						.getHousenumber());                                                   //This is a candidate for a name change
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

		System.out.println("Phone List = " + thePhoneList.size());  //This is a candidate for a name change

		for (int i = 0; i < thePhoneList.size(); i++) {   // check each phone in the List, //This is a candidate for a name change

			Phone curphone = (Phone) thePhoneList.get(i);    // cast list element to Phone Class //This is a candidate for a name change												
			System.out.println("Testing Phone" + " " + curphone.getPid());

			if (curphone.getPid().equalsIgnoreCase("dostoyjewski")) {     // check for the author //This is a candidate for a name change

				answer = "Phone by : " + curphone.getPid() + "\n"  //This is a candidate for a name change
						+ " Phone Model: " + curphone.getBrand()      //This is a candidate for a name change
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
