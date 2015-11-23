package Examples;

import java.io.*;
import java.util.*;

// We are only working with products here, so no need to import the other 
// generated classes 

import Examples.Product;

public class JAXB_ExampleUse {

	// This example class uses the JAXB_XMLParser class to generate objects of the Class product for all 
	// products listed in the Library described in the Library.xml file

	public static void main(String[] args) {

		JAXB_XMLParser xmlhandler = new JAXB_XMLParser();   // we need an instance of our parser	
		File xmlfiletoload = new File("Phone.xml");	    // we need a file (xml) to load
		List xmlobjectlist = new ArrayList();               // we need a List to store the objects (products) in   

		try {
			FileInputStream readthatfile = new FileInputStream(xmlfiletoload);  // initiate input stream
			xmlobjectlist = xmlhandler.loadXML(readthatfile);                   // execute parser, get list 
			Iterator<Phone> gothruphone = xmlobjectlist.iterator();              // initiate iterator on list

			// Remember: xmlobjectlist is our "product" object list, containing all product objects

			// Iterate through all products and dump their info to the console 

			while(gothruphone.hasNext()){   // print out information off all products using an Iterator 

				Phone curproduct = gothruphone.next();
				System.out.println("Battery Life = " + curproduct.getBattery().trim() +
						"\t\t Pixels = " + curproduct.getCameraPixels().trim() +
						"\t\t Capacity = " + curproduct.getCapacity().trim());				    
			}

			// Select a product with a distinct author, in this example we are looking for a product with an author: 'dostoyjewski'

			for(int i = 0; i < xmlobjectlist.size(); i++ ){                   // check each product in the list 

				Product curproduct = (Product)xmlobjectlist.get(i);                    // cast list element to product

				if(curproduct.getName().equalsIgnoreCase("dostoyjewski")){      // check the author

					System.out.println("A product written by " + curproduct.getName() +
							" is for example the classic " + curproduct.getBrand() + ".");							 	
				}				    				    
			}     // for loop closed 

		} 

		catch (FileNotFoundException e) {e.printStackTrace(); e.printStackTrace();} 		    
		catch(Exception e){e.printStackTrace();}
	}
}
