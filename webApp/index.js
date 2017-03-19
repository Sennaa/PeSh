var java = require("java");
var cheerio = require('cheerio');
var fs = require('fs');

java.classpath.push("peshWebApp.jar");

var bodyParser = require('body-parser');
var express = require('express');
var uuid = require('uuid');
var cookieParser = require('cookie-parser');
var session = require('express-session');
var app = express();

app.use(cookieParser());
app.use(session({ 
 secret: uuid.v4(),
 resave: false,
 saveUninitialized: true
}));

// Create a new instance (recommender) for each recommendation type
//var peshMain			= java.newInstanceSync("si.personalshopper.UserMains");
var ArrayList = java.import('java.util.ArrayList');
var Collections = java.import('java.util.Collections');
var userInstances = Collections.synchronizedListSync(new ArrayList());
var userIDs		  = Collections.synchronizedListSync(new ArrayList());

app.use(bodyParser.urlencoded({
	extended: true
}));
app.use(bodyParser.json());

// Show index.html, containing introduction, explanation and consent.
app.get('/', function(req, res) {
	console.log('Index');
	var sID = req.sessionID;
	console.log(sID);
	if (userIDs.containsSync(sID)) {
	}
	else {
		var peshMain = java.newInstanceSync("si.personalshopper.Main", sID);
		userInstances.addSync(peshMain);
		userIDs.addSync(sID);
		console.log(userInstances.sizeSync() + ", " + userIDs.sizeSync());
	}
	res.sendFile(__dirname + '/index.html');
});

// Show persona.html, add personas dynamically let user choose which persona fits best.
app.all('/persona.html', function(req, res) {
	if (req.method == 'GET') {
		console.log('Persona keuze');
		/*--- Load personas ---*/
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		var persona_keuze = peshMain.getPersonaHTMLSync();
		var personaFile = cheerio.load(fs.readFileSync(__dirname + '/persona.html'));
		var personas = cheerio.load(persona_keuze)
		personaFile(".table-persona-element").append(personas.html());
		res.end(personaFile.html());
	}
	else if (req.method == 'POST') {
		var persona = req.body.personaButton;
		console.log(persona);
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		if (persona.length > 0) {
			peshMain.setPersonaSync(persona);
			res.redirect('/uitleg-vragenlijst-1.html');
		}
		else {
			res.redirect('/persona.html');
		}
	}
});

// Show uitleg-vragenlijst-1.html
app.get('/uitleg-vragenlijst-1.html', function(req, res) {
	console.log('Uitleg vragenlijst 1');
	res.sendFile(__dirname + '/uitleg-vragenlijst-1.html');
});

// Show vragenlijst_een_een.html, add shops dynamically.
app.all('/vragenlijst-1-1.html', function(req, res) {
	if (req.method == 'GET') {
		var sID = req.sessionID;
		console.log("Session ID: " + sID);
		var index = userIDs.indexOfSync(sID);
		console.log("Index: " + index);
		var peshMain = userInstances.getSync(index);
		var coldStartIters 		= peshMain.getColdStartItersSync();
		console.log("Iteration " + coldStartIters);
		// After the second time, go to vragenlijst-2-1
		if (coldStartIters > 2) {
			res.redirect('/uitleg-vragenlijst-2.html');
		} 
		else {
			console.log('Vragenlijst 1.1');
			var leftRecommenderName;
			var rightRecommenderName;
			// The first time, use either composite or contentbased
			if(coldStartIters == 1) {
				// If Math.random() >= 0.5, random left, other right
				if (Math.random() >= 0.5) {
					leftRecommenderName = "random";
					rightRecommenderName = peshMain.getFirstOrLeftRecommenderSync();
				}
				// Else, other left, random right
				else {
					leftRecommenderName = peshMain.getFirstOrLeftRecommenderSync();
					rightRecommenderName = "random";
				}
				console.log(leftRecommenderName);
				console.log(rightRecommenderName);
			}
			// The second time, use the other recommendersystem
			else {
				// If Math.random() >= 0.5, random left, other right
				if (Math.random() >= 0.5) {
					leftRecommenderName = "random";
					rightRecommenderName = peshMain.getSecondOrRightRecommenderSync();
				}
				// Else, other left, random right
				else {
					leftRecommenderName = peshMain.getSecondOrRightRecommenderSync();
					rightRecommenderName = "random";
				}
				console.log(leftRecommenderName);
				console.log(rightRecommenderName);
			}
			// Make HTML
			var shopSize = peshMain.calculateRouteAndShopSizeSync(leftRecommenderName, rightRecommenderName);
			var leftRecommendation = peshMain.getRecommendationHTMLSync(leftRecommenderName, "left", shopSize);
			var rightRecommendation = peshMain.getRecommendationHTMLSync(rightRecommenderName, "right", shopSize);
			var recommendationFile = cheerio.load(fs.readFileSync(__dirname + '/vragenlijst-1-1.html'));
			var leftTableHTML = cheerio.load(leftRecommendation);
			var rightTableHTML = cheerio.load(rightRecommendation);
			recommendationFile("#lists").append(leftTableHTML.html());
			recommendationFile("#lists").append(rightTableHTML.html());
			peshMain.setRecommendersSync(leftRecommenderName, rightRecommenderName);
			peshMain.setRecommendation1File(recommendationFile.html());
			res.end(recommendationFile.html());
		}
	}
	else if (req.method == 'POST') {
		var sID = req.sessionID;
		console.log("Session ID: " + sID);
		var index = userIDs.indexOfSync(sID);
		console.log("Index: " + index);
		var peshMain = userInstances.getSync(index);
		var persona = peshMain.getPersonaNameSync();
		console.log(req.body.vragenlijstButton);
		preference = req.body.vragenlijstButton; // "left" or "right"
		var leftRecommenderName = peshMain.getLeftRecommenderSync();
		var rightRecommenderName = peshMain.getRightRecommenderSync();
		if (preference == "left") {
			peshMain.saveColdStartPrefRecSync(sID, persona, leftRecommenderName, rightRecommenderName, leftRecommenderName);
			peshMain.addIterationSync();
			res.redirect('/vragenlijst-1-2.html');
		}
		else if (preference == "right"){
			peshMain.saveColdStartPrefRecSync(sID, persona, leftRecommenderName, rightRecommenderName, rightRecommenderName);
			peshMain.addIterationSync();
			res.redirect('/vragenlijst-1-2.html');
		}
		else {
			var recommendationFile = cheerio.load(peshMain.getRecommendation1FileSync());
			res.end(recommendationFile.html());
		}
	}
});

// Show vragenlijst_een_twee.html, add shops dynamically.
app.all('/vragenlijst-1-2.html', function(req, res) {
	console.log('Vragenlijst 1.2');
	if (req.method == 'GET') {
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		var combinedRecommendations = peshMain.getCombinedRecommendedShopsHTMLSync();
		var recommendationFile = cheerio.load(fs.readFileSync(__dirname + '/vragenlijst-1-2.html'));
		var recommendationHTML = cheerio.load(combinedRecommendations);
		recommendationFile(".list-group").append(recommendationHTML.html());
		res.end(recommendationFile.html());
	}
	else if (req.method == 'POST') {
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		var persona = peshMain.getPersonaNameSync();
		console.log(req.body.vragenlijstButton);
		var posFeedbackString = req.body.vragenlijstButton;
		var leftRecommenderName = peshMain.getLeftRecommenderSync();
		var rightRecommenderName = peshMain.getRightRecommenderSync();
		peshMain.saveColdStartPrefShopsSync(sID, persona, leftRecommenderName, posFeedbackString);
		console.log("first recommender saved");
		peshMain.saveColdStartPrefShopsSync(sID, persona, rightRecommenderName, posFeedbackString);
		
		//var posFeedbackList = posFeedbackString.split(";");
		//posFeedbackList.pop(); // Last element is always empty, so delete it
		//for (var shop = 0 ; shop < posFeedbackList.length ; shop++) {
		//	peshMain.addFeedbackSync(posFeedbackList[shop]);
		//}		
		//peshMain.confirmRecommendationSync();
		res.redirect('/vragenlijst-1-1.html');
	}
});

// Show uitleg-vragenlijst-2.html
app.get('/uitleg-vragenlijst-2.html', function(req, res) {
	console.log('Uitleg vragenlijst 2');
	res.sendFile(__dirname + '/uitleg-vragenlijst-2.html');
});

// Show vragenlijst_twee_een.html, add shops dynamically.
app.all('/vragenlijst-2-1.html', function(req, res) {
	if (req.method == 'GET') {
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		var recommendersIters 	= peshMain.getRecommendersItersSync();
		console.log("RecommendersIters: " + recommendersIters);
		// After the 15th iteration, go to experiment-einde.html
		if (recommendersIters > 15) {
			res.redirect('/experiment-einde.html');
		}
		else {
			console.log('Vragenlijst 2.1');
			var leftRecommenderName = peshMain.getFirstOrLeftRecommenderSync();
			var rightRecommenderName = peshMain.getSecondOrRightRecommenderSync();
			var shopSize = peshMain.calculateRouteAndShopSizeSync(leftRecommenderName, rightRecommenderName);
			var leftRecommendation = peshMain.getRecommendationHTMLSync(leftRecommenderName, "left", shopSize);
			var rightRecommendation = peshMain.getRecommendationHTMLSync(rightRecommenderName, "right", shopSize);
			var recommendationFile = cheerio.load(fs.readFileSync(__dirname + '/vragenlijst-2-1.html'));
			var leftTableHTML = cheerio.load(leftRecommendation);
			var rightTableHTML = cheerio.load(rightRecommendation);
			recommendationFile("#lists").append(leftTableHTML.html());
			recommendationFile("#lists").append(rightTableHTML.html());
			console.log("Left: " + leftRecommenderName);
			console.log("Right: " + rightRecommenderName);
			peshMain.setRecommendersSync(leftRecommenderName, rightRecommenderName);
			peshMain.setRecommendation2File(recommendationFile.html());
			res.end(recommendationFile.html());
		}
	}
	else if (req.method == 'POST') {
		console.log(req.body.vragenlijstButton);
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		var persona = peshMain.getPersonaNameSync();
		var leftRecommenderName = peshMain.getLeftRecommenderSync();
		var rightRecommenderName = peshMain.getRightRecommenderSync();
		preference = req.body.vragenlijstButton; // "left" or "right"
		if (preference == "left") {
			peshMain.saveIterationsPrefRecSync(sID, persona, leftRecommenderName);
			peshMain.addIterationSync();
			res.redirect('/vragenlijst-2-2.html');
		}
		else if (preference == "right"){
			peshMain.saveIterationsPrefRecSync(sID, persona, rightRecommenderName);
			peshMain.addIterationSync();
			res.redirect('/vragenlijst-2-2.html');
		}
		else {
			var recommendationFile = cheerio.load(peshMain.getRecommendation2FileSync());
			res.end(recommendationFile.html());
			// do nothing
		}
	}
});

// Show vragenlijst_twee_twee.html, add shops dynamically.
app.all('/vragenlijst-2-2.html', function(req, res) {
	console.log('Vragenlijst 2.2');
	if (req.method == 'GET') {
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		var combinedRecommendations = peshMain.getCombinedRecommendedShopsHTMLSync();
		var recommendationFile = cheerio.load(fs.readFileSync(__dirname + '/vragenlijst-2-2.html'));
		var recommendationHTML = cheerio.load(combinedRecommendations);
		recommendationFile(".list-group").append(recommendationHTML.html());
		res.end(recommendationFile.html());
	}
	else if (req.method == 'POST') {
		console.log(req.body.vragenlijstButton);
		var sID = req.sessionID;
		var index = userIDs.indexOfSync(sID);
		var peshMain = userInstances.getSync(index);
		var persona = peshMain.getPersonaNameSync();
		var posFeedbackString = req.body.vragenlijstButton;
		peshMain.saveIterationsPrefShopsSync(sID, persona, "composite", posFeedbackString);
		peshMain.saveIterationsPrefShopsSync(sID, persona, "contentbased", posFeedbackString);
		
		var posFeedbackList = posFeedbackString.split(";");
		for (var shop = 0 ; shop < posFeedbackList.length ; shop++) {
			peshMain.addFeedbackSync(posFeedbackList[shop]);
		}		
		peshMain.confirmRecommendationSync();
		res.redirect('/vragenlijst-2-1.html');
	}
});

app.get('/experiment-einde.html', function(req, res) {
	console.log('Experiment einde');
	req.session.destroy();
	res.sendFile(__dirname + '/experiment-einde.html');
});

app.listen(3000, function() {
	console.log('App listening on port 3000!');
});