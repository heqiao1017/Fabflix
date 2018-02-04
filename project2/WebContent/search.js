function handleSearchResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	
	console.log("handle search response");
	console.log(resultDataJson);
	//will always direct to movieList.html page, but can be "No Result Found" or movie list
	var movieTableBodyElement = jQuery("#movie_table_body");
	if (jQuery.isEmptyObject(resultDataJson)) {
		var rowHTML = "<tr><td><h2>No Result Found</h2></td></tr>";
		movieTableBodyElement.append(rowHTML);
	}
	else {
		var rowHTML = "<tr><th>ID</th><th>Title</th><th>Year</th><th>Director</th><th>Genres</th><th>Stars</th></tr>";
		movieTableBodyElement.append(rowHTML);
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


function empty() {
    var title = document.getElementById("title").value;
    var year = document.getElementById("year").value;
    var director = document.getElementById("director").value;
    var firstname = document.getElementById("firstname").value;
    var lastname = document.getElementById("lastname").value;
    if (title == "" && year == "" && director =="" && firstname=="" && lastname=="") {
        alert("Invalid input, cannot be empty!");
        return false;
    };
}
