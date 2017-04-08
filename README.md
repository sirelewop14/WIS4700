This class provides several methods for utilizing LDA Analysis for topic 
identification. The purpose of these functions is to allow a user to input
a CSV file containing users and tweets from Twitter. The functions provided
here are then used to parse the tweets, stem and remove stop words, and 
then estimate an LDA model based of the data.

Once the model has been created, an inference can be performed on the CSV 
data. When inference has been performed, the user can then run the evaluation
step. This step analyzes the raw data and calculates the weight of identified
topics for individual users.

Finally, once the weight of each topic per user has been calculated, the 
labeling function can provide a report in CSV format w/ vectors for the 
selected topics and labels. The final function SHOULD only be run once the 
generated tword file (From inferencing step) has been read through, and topics
of interest have been identified and labeled.