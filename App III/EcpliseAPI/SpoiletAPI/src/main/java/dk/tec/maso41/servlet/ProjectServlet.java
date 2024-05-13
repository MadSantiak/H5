package dk.tec.maso41.servlet;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.tec.maso41.AnalyzeRequest;
import dk.tec.maso41.Toilet;

import java.util.logging.Logger;

public class ProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * Allows the API to respond to get-methods.
		 * Using AnalyzeRequest to parse whether the request is asking for "All Toilets"
		 * or a specific recordset.
		 * Using ObjectMapper to write the data in the response to be read by the
		 * App's ApiLayer.
		 * Expanded to allow for additional Object types (Haircolor and Programming language)
		 */
		PrintWriter out = response.getWriter();
		AnalyzeRequest analyze = new AnalyzeRequest(request.getPathInfo());
		ObjectMapper mapper = new ObjectMapper();
		DBTools db = new DBTools();
		
		switch (analyze.getMatch()) {
			case MatchToiletId:
				Toilet p = db.getToiletById(analyze.getId());
				String json = mapper.writeValueAsString(p);
				out.print(json);
				break;
			case MatchToilet:
				List<Toilet> people = db.getAllToilet();
				String jsonAll = mapper.writeValueAsString(people);
				out.print(jsonAll);
				break;
			
			case MatchNo:
				out.write("\nNot a valid request..");
				break;
		default:
			break;
			}
		}
	
	/**
	 * Responds to POST-requests, calling the corresponding functions in DBTools to create the object requested.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		DBTools db = new DBTools();
		BufferedReader reader = req.getReader();
		String recJSON = reader.readLine();
		ObjectMapper mapper = new ObjectMapper();
		AnalyzeRequest analyze = new AnalyzeRequest(req.getPathInfo());
		
		switch(analyze.getMatch())
		{
			case MatchToilet:
				Toilet p = mapper.readValue(recJSON, Toilet.class);
				String resToilet = mapper.writeValueAsString(db.addToilet(p));
				out.print(resToilet);
				break;
			
			default:
				break;
		}
		
		
		
	}

	/**
	 * Allows the App to call the API and then, via the DBtools class
	 * instruct the database to remove the row with the ID fetched from the 
	 * Toilet object in question, passed along in the Path of the request,
	 * which is finally extracted using the AnalyseRequest class.
	 * Expanded to include other Object types.
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AnalyzeRequest analyze = new AnalyzeRequest(req.getPathInfo());
		DBTools db = new DBTools();
		int delId = analyze.getId();
		System.out.println(req.getPathInfo());
		System.out.println("DELETING RECORD " + delId);
		switch(analyze.getMatch())
		{
			
			case MatchToiletId:
				System.out.print("Toilet");
				db.delToilet(delId);
				break;
			
			default:
				break;
		}
	}

	/**
	 * Not expanded as only "Toilets" can be updated in the App, and the option to update Toilet or Programming Language objects
	 * was deemed irrelevant, semantically speaking (we presume people proofread names when they create new color designations/programming languages).
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AnalyzeRequest analyze = new AnalyzeRequest(req.getPathInfo());
		ObjectMapper mapper = new ObjectMapper();
		DBTools db = new DBTools();
		BufferedReader reader = req.getReader();
		String updJSON = reader.readLine();
		Toilet p = mapper.readValue(updJSON, Toilet.class);
		db.updateToilet(p);
	}
	
}
