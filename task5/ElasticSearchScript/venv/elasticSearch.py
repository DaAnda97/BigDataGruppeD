import sys
import json
import requests

def checkInput():
    if len(sys.argv) <= 2:
        print("Not enough arguments given");
        print("Usage: [option] [document/term]");
        print("-d: fetch the most similar documents");
        print("-t: fetch the most related terms")
        exit();
    elif len(sys.argv) > 4:
        print("Too many arguments");

def parseCommandParametersAndExecuteByOption():
    checkInput();
    option = getOption();
    termToSearch = getSearchParamter();
    executeActionByOption(option, termToSearch);

def getOption():
    return sys.argv[1];

def getSearchParamter():
    return sys.argv[2];

def executeActionByOption(option, term):
    response = getJsonResponse(option, term);
    print("-----------------")
    print("Result:")
    print("-----------------")
    for document in response:
        print(document)

def executeMostRelatedTermOption(option):
    print("Fetching the 20 most related terms for chosen term");

def getJsonResponse(option, term):
    session = requests.Session();
    session.auth = ("elastic", "changeme")

    # similar document search
    if(option == '-d'):
        response = session.get("http://localhost:9200/document/_doc/" + term);
        responseJson = json.loads(response.text);
        # Check if there are any results
        resultsFound = responseJson['found'];
        if (str(resultsFound) == "False"):
            print("The query returned 0 results");
            exit();
        tmp = responseJson['_source'];
        return tmp['mostRelevantDocs']


    # most relevant documents for given term
    elif(option == '-t'):
        response = session.get("http://localhost:9200/term/_source/" + term);
        responseJson = json.loads(response.text);
        # Check if there are any results
        if ("error" in responseJson):
            print("The query returned 0 results");
            exit();
        return responseJson['mostRelatedDocs'];

parseCommandParametersAndExecuteByOption();

