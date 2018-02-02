function handleSearchResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	
	console.log("handle search response");
	console.log(resultDataJson);
	//will always direct to movieList.html page, but can be "No Result Found" or movie list
	window.location.replace("/project2/movieList.html");
	var movieTableBodyElement = jQuery("#movie_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
		rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
		rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
		rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
		rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
		rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>";
		rowHTML += "</tr>"
		movieTableBodyElement.append(rowHTML);
	}
}


function submitSearchForm(formSubmitEvent) {
	console.log("submit search form");
	
	// important: disable the default action of submitting the form
	//   which will cause the page to refresh
	//   see jQuery reference for details: https://api.jquery.com/submit/
	formSubmitEvent.preventDefault();
		
	jQuery.post(
		"/project2/Search", 
		// serialize the login form to the data sent by POST request
		jQuery("#search_form").serialize(),
		(resultDataString) => handleSearchResult(resultDataString)
	);

}

// bind the submit action of the form to a handler function
jQuery("#search_form").submit((event) => submitSearchForm(event));

