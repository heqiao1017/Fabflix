$("#browsebygenre").click(function(){
	window.location.href='./browsebygenre.html';
});
$("#browsebytitle").click(function(){
	window.location.href='./browsebytitle.html';
});
//<div><input class="search_input" style="color:#000" type="text" name="searchbox" id="autocomplete"/></div>

/*
 * CS 122B Project 4. Autocomplete Example.
 * 
 * This Javascript code uses this library: https://github.com/devbridge/jQuery-Autocomplete
 * 
 * This example implements the basic features of the autocomplete search, features that are 
 *   not implemented are mostly marked as "TODO" in the codebase as a suggestion of how to implement them.
 * 
 * To read this code, start from the line "$('#autocomplete').autocomplete" and follow the callback functions.
 * 
 */

//https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Map
var movieMap = new Map();
var starMap = new Map();
var map = new Map();


/*
 * This function is called by the library when it needs to lookup a query.
 * 
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	
	// TODO: if you want to check past query results first, you can do it here
	var pure_query = query.trim().toLowerCase();
	var query_cached = false;
	//var data;
	console.log("---->check past query results first");
	for (var [key, value] of map.entries()) {
		if (key.startsWith(pure_query)) {
			query_cached = true;
			console.log("=>from cahce suggestion list: \n"+JSON.stringify(value));//suggestion list
			doneCallback({suggestions: value});
			break;
		}
	}
	
	if (!query_cached) {
		console.log("--->sending AJAX request to backend Java Servlet")
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			
			//url need to be modified
			"url": "/project2/MovieSuggestion?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
	
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	console.log("=>server response suggestion list: \n"+JSON.stringify(data));
	
	//if you want to cache the result into a global variable you can do it here
	var pure_query = query.trim().toLowerCase();
	console.log("--->Cache the result into a global variable");
	map.set(pure_query, data);
	
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: data } );
}


/*
 * This function is the select suggestion hanlder function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	//jump to the specific result page based on the selected suggestion
	console.log("you select " + suggestion["value"])
//	var url = suggestion["data"]["category"] + "-hero" + "?id=" + suggestion["data"]["heroID"]
//	console.log(url)
	if (suggestion["data"]["category"] === "Movie") {
		jQuery.ajax({
			"method": "GET",
			"dataType": "json",
			"url": "/project2/SingleMovie?title=" +  suggestion["value"],
			"success": function(resultDataString) {
				//console.log(resultDataString);
				localStorage.setItem('singleMovie', JSON.stringify(resultDataString));
				window.location.href = "/project2/singlemovie.html";
			},
			"error": function(errorData) {
				console.log("displaySingleMovie ajax error")
				console.log(errorData)
			}
		})
	}
	else {
		jQuery.ajax({
			"method": "GET",
			"dataType": "json",
			"url": "/project2/singleStar?star=" + suggestion["value"],
			"success": function(resultDataString) {
				//console.log(resultDataString);
				localStorage.setItem('singleStar', JSON.stringify(resultDataString));
				window.location.href = "/project2/singlestar.html";
			},
			"error": function(errorData) {
				console.log("displayStar ajax error")
				console.log(errorData)
			}
		})
	}
}


/*
 * This statement binds the autocomplete library with the input box element and 
 *   sets necessary parameters of the library.
 * 
 * The library documentation can be find here: 
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 * 
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set the groupby name in the response json data field
    groupBy: "category",
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // add other parameters, such as mininum characters
    minChars : 3,
});



function storageAvailable(type) {
    try {
        var storage = window[type],
            x = '__storage_test__';
        storage.setItem(x, x);
        storage.removeItem(x);
        return true;
    }
    catch(e) {
        return e instanceof DOMException && (
            // everything except Firefox
            e.code === 22 ||
            // Firefox
            e.code === 1014 ||
            // test name field too, because code might not be present
            // everything except Firefox
            e.name === 'QuotaExceededError' ||
            // Firefox
            e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
            // acknowledge QuotaExceededError only if there's something already stored
            storage.length !== 0;
        
    }
}

function saveToLocalStorage(resultDataString) {
	if (storageAvailable('localStorage')) {
		  // Yippee! We can use localStorage awesomeness
		console.log(resultDataString);
		localStorage.setItem('myMainKey', JSON.stringify(resultDataString));
		window.location.href = "/project2/movielist.html";
	}
	else {
		console.log("there is no storage!!!!!!!!")
	}
}

/*
 * do normal full text search if no suggestion is selected (DONE!)
 */
function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	//you should do normal search here
	if (query !== "") {
		jQuery.ajax({
			"method": "GET",
			"dataType": "json",
			"url": "/project2/Search?query=" + escape(query),
			"success": (resultData) => saveToLocalStorage(resultData),
			"error": function(errorData) {
				console.log("handleNormalSearch ajax error")
				console.log(errorData)
			}
		})
	}
	else {
		alert("input cannot be empty!");
	}
}

// bind pressing enter key to a hanlder function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the hanlder function
		handleNormalSearch($('#autocomplete').val());
	}
})

//if you have a "search" button, you may want to bind the onClick event as well of that button
function searchfulltext() {
	handleNormalSearch($('#autocomplete').val());
}
