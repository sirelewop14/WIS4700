/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wis4700;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author rhys
 */
public class WIS4700 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO - Change this with the geo-coordinates for your selected city
           // {{SW_lng, WS_at},{NE_lng,NE_lat}}
		double[][] boundingBox = {{-84.3113769,39.7018339},{-84.0929378,39.9208229}};
		
		/**
		 * Step 1 - Set up a ConfigurationBuilder object with your Twitter access tokens.
		 */
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled( true );
		
		// TODO - Fill with your API keys
		// Set consumer key here
		cb.setOAuthConsumerKey("OacKkdgqWDdhyUhcv5RPYTAsV");
		// Set consumer secret here
		cb.setOAuthConsumerSecret("xTPPfVU2u2cQCPpGH0ZGQ9HtK4v5fkXaxiatl1VnXUtmNUnDob");
		// Set consumer access token here 
		cb.setOAuthAccessToken("818303101404844033-7rVNAUZtcth8E44PihxghTiSBvaHPkC");
		// Set consumer access token secret here
		cb.setOAuthAccessTokenSecret("iJeo03Z0i5x5y9RSXLm80KUnWVlK6iuxGFwNxoaBt3xkd");
		
		cb.setJSONStoreEnabled( true );
		
		/**
		 * Step 2 - Create an object of Twitter Factory Class
		 */
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

		/**
		 * Step 3 - Create a Filter Query instance and set the bounding box as the filtering condition.
		 * Use locations() method of FilterQuery to do this.
		 */
		FilterQuery fq = new FilterQuery();
                fq.locations(boundingBox);
		
		
		/**
		 * Step 4 - Implement Status Listener Interface. Print the Tweets inside this method.
		 */
		StatusListener listener = new StatusListener()
                
		{
			@Override
			public void onStatus( Status status )
			{
				// You will collect and print tweets here
                                System.out.println(status);
				
			}

			@Override
			public void onDeletionNotice( StatusDeletionNotice statusDeletionNotice )
			{
                            System.out.println(statusDeletionNotice.toString());
			}

			@Override
			public void onTrackLimitationNotice( int numberOfLimitedStatuses )
			{
                            System.out.println("Over Limit By: "+numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo( long userId, long upToStatusId )
			{
                            System.out.println("UserID: " + userId + "upToStatusID: "+upToStatusId);
			}

			@Override
			public void onStallWarning( StallWarning warning )
			{
				System.out.println(warning.toString());
			}

			@Override
			public void onException( Exception e )
			{
				e.printStackTrace();
			}
		};
		
		/**
		 * Step 4 - Add the StatusListener instance to the TwitterStream object.
		 */
		twitterStream.addListener(listener);
		
		/**
		 * Step 5 - Call the filter() method on TwitterStream object passing FilterQuery object as an argument
		 * to start collecting tweets.
		 */
		twitterStream.filter(fq);
                
		
	}
    }

