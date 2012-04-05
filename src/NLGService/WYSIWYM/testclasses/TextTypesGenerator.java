package NLGService.WYSIWYM.testclasses;

import java.io.FileWriter;
import java.io.PrintWriter;

import NLGService.WYSIWYM.model.ContentPlan;
import NLGService.WYSIWYM.model.FeedbackText;
import NLGService.WYSIWYM.model.SemanticGraph;
import NLGService.WYSIWYM.ontology.OntologyReader;
import NLGService.WYSIWYM.ontology.SesameReader;
import NLGService.WYSIWYM.transformer.BrowsingGenerator;
import NLGService.WYSIWYM.transformer.ContentPlanner;
import NLGService.WYSIWYM.transformer.SemanticGraphTransformer;
import NLGService.WYSIWYM.transformer.SurfaceRealiser;
import NLGService.WYSIWYM.util.SesameException;
import NLGService.WYSIWYM.util.SurfaceRealisationException;
import NLGService.WYSIWYM.util.TextPlanningException;

/**	Acts as a BrowsingGenerator, but generates not one but three feedback texts,
 *	of different types, for the text comparison experiment.
 *
 *	@author	Feikje Hielkema
 *	@version 1.0 22-01-2009
 */
public class TextTypesGenerator extends BrowsingGenerator
{
	private FeedbackText condition1, condition2;
	/** Maximum number of similar properties before this property is aggregated */
	private int aggregationThreshold = 4;
	
	/**	Constructor.
	 *	@param sgt SemanticGraphTransformer
	 *	@param reader Ontology
	 *	@param s Sesame
	 *	@param userID User ID
	 *	@throws SesameException
	 */
	public TextTypesGenerator(SemanticGraphTransformer sgt, OntologyReader reader, SesameReader s, String userID) throws SesameException
	{
		super(sgt, reader, s, userID, false);
	}
	
	/**	Constructor.
	 *	@param sgt SemanticGraphTransformer
	 *	@param reader Ontology
	 *	@param noAnchors If true, don't include anchors in the feedback text
	 *	@param s Sesame
	 *	@param userID User ID
	 *	@throws SesameException
	 */
	public TextTypesGenerator(SemanticGraphTransformer sgt, OntologyReader reader, boolean noAnchors, SesameReader s, String userID) throws SesameException
	{
		super(sgt, reader, noAnchors, s, userID);
	}
	
	/**	Constructor.
	 *	@param graph SemanticGraph
	 *	@param r Ontology
	 *	@param s Sesame
	 */
	public TextTypesGenerator(SemanticGraph graph, OntologyReader r, SesameReader s)
	{
		super(graph, r, s);
	}
	
	/**	Updates the feedbacktext when the semantic graph has been changed.
	 *	@throws	SurfaceRealisationException
	 *	@throws TextPlanningException
	 */
	public void updateText() throws SurfaceRealisationException, TextPlanningException//,IOException
	{	//plan text
		getGraph().stopFlashing();	//don't need the flashing here!
	
		ContentPlan plan = new ContentPlanner(reader, getGraph(), aggregationThreshold).plan();
		SurfaceRealiser sr = new SurfaceRealiser();
		setText(new FeedbackText(sr.realise(plan, false)));
		
		toFile(getText(), "Condition3");
		getText().reset();	//reset this text so it can be shown in the normal way as well
		
		//generate and store the other versions (only need one version for NLG service)
//		condition1 = generateCondition(1);
//		condition2 = generateCondition(2);
//		toFile(condition1, "Condition1");
//		toFile(condition2, "Condition2");
	}
	
	/** Returns the feedbacktext as a String.
	 *	@param condition 1 for simple condition, 2 for slight aggregation, 3 for syntactic aggregation
	 *	@return String
	 */
	public String getText(int condition)
	{
		FeedbackText t = null;
		switch (condition)
		{
			case 1: t = condition1; break;
			case 2: t = condition2; break;
			case 3: t = getText(); break;
			default: return null;
		}
		
		StringBuffer sb = new StringBuffer();
		t.reset();
		while (t.hasNext())
			sb.append(t.next().toString());
		return sb.toString();
	}
		
	private FeedbackText generateCondition(int condition)
	{
		try
		{
			ContentPlan plan = new TextTypesPlanner(reader, getGraph()).plan(condition);
			SurfaceRealiser sr = new SurfaceRealiser();
			return new FeedbackText(sr.realise(plan, false));
		} 
		catch (Exception e)
		{
			System.out.println("COULD NOT GENERATE CONDITION " + condition);
			e.printStackTrace();
			return null;
		}
	}	
		
	private void toFile(FeedbackText text, String filename)
	{
		if (text == null)
			return;
		try
    	{
	    	FileWriter fw = new FileWriter(filename + ".txt");
			PrintWriter w = new PrintWriter(fw);
			while (text.hasNext())
				w.print(text.next().toString());
			w.close();
			fw.close();
		}
		catch(Exception e)
		{}
	}

	public int getAggregationThreshold() {
		return aggregationThreshold;
	}

	public void setAggregationThreshold(int aggregationThreshold) {
		this.aggregationThreshold = aggregationThreshold;
	}
}