function handleSearchResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	
	console.log("handle search response");
	console.log(resultDataJson);
	//will always direct to movieList.html page, but can be "No Result Found" or movie list
//	window.location.replace("/project2/movielist.html");
	var movieTableBodyElement = jQuery("#movie_table_body");
	for (var i = 0; i < resultDataJson.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<th>" + resultDataJson[i]["movie_id"] + "</th>";
		rowHTML += "<th>" + resultDataJson[i]["movie_title"] + "</th>";
		rowHTML += "<th>" + resultDataJson[i]["movie_year"] + "</th>";
		rowHTML += "<th>" + resultDataJson[i]["movie_director"] + "</th>";
		rowHTML += "<th>" + resultDataJson[i]["movie_genres"] + "</th>";
		rowHTML += "<th>" + resultDataJson[i]["movie_stars"] + "</th>";
		rowHTML += "</tr>"
		movieTableBodyElement.append(rowHTML);
	}
}

function getQueryString() {
	  var result = {}, queryString = location.search.slice(1),
	      re = /([^&=]+)=([^&]*)/g, m;

	  while (m = re.exec(queryString)) {
	    result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
	  }

	  return result;
	}
//
//var myParam = getQueryString()["title"];
//console.log(myParam);

jQuery.ajax({
	data: getQueryString(),
	dataType: "json",
	method: "GET",
	url: "/project2/Search",
	success: (resultData) => handleSearchResult(resultData)
});
