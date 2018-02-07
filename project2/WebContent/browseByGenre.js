function splitListItems(listItems) {
	var array = listItems.split(',');
	return array;
}

function handleSearchResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	
	console.log("handle search response");
	console.log(resultDataJson);
	//will always direct to movieList.html page, but can be "No Result Found" or movie list
	var movieTableBodyElement = jQuery("#movie_table_body");
	var movieTableHeadElement = jQuery("#movie_head");
	if (jQuery.isEmptyObject(resultDataJson)) {
		var rowHTML = "<tr><td><h2>No Result Found</h2></td></tr>";
		movieTableHeadElement.append(rowHTML);
	}
	else {
		var rowHTML = "<tr><th>ID</th><th onclick=\"sortTable(1)\"><a href=\"#\">Title</a></th><th onclick=\"sortTable(2)\"><a href=\"#\">Year</a></th><th>Director</th><th>Genres</th><th>Stars</th></tr>";
		movieTableHeadElement.append(rowHTML);
		for (var i = 0; i < resultDataJson.length; i++) {
			var rowHTML = "";
			rowHTML += "<tr>";
			rowHTML += "<th>" + resultDataJson[i]["movie_id"] + "</th>";
			rowHTML += "<th><form id=\"title_form"+i+"\" action=\"/project2/singlemovie.html\" method=\"get\"><input type=\"hidden\" value=\""+resultDataJson[i]["movie_title"]+"\" name=\"title\"><a href=\"#\" onclick=\"document.getElementById('title_form"+i+"').submit();\" id = \"title_achor\">"+resultDataJson[i]["movie_title"]+"</a></form></th>";
			rowHTML += "<th>" + resultDataJson[i]["movie_year"] + "</th>";
			rowHTML += "<th>" + resultDataJson[i]["movie_director"] + "</th>";
			rowHTML += "<th>" + resultDataJson[i]["movie_genres"] + "</th>";
			rowHTML += "<th>";
			var star_arr = splitListItems(resultDataJson[i]["movie_stars"]);
			for (var j = 0; j < star_arr.length; j++) {
				rowHTML += "<form id='star_form"+j+"' action=\"/project2/singlestar.html\" method=\"get\"><input type=\"hidden\" value=\""+star_arr[j]+"\" name=\"star\"><a href=\"#\" onclick=\"document.getElementById('star_form"+j+"').submit();\">"+star_arr[j]+"</a></form>"
			}
			rowHTML += "</th>";

//			rowHTML += "<th>" + resultDataJson[i]["movie_stars"] + "</th>";
			rowHTML += "</tr>"
			movieTableBodyElement.append(rowHTML);
		}
	}

}


function getQueryString() {
	var result = {}, queryString = location.search.slice(1), re = /([^&=]+)=([^&]*)/g, m;
	
	while (m = re.exec(queryString)) {
		result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
	}
	return result;
}

jQuery.ajax({
	data: getQueryString(),
	dataType: "json",
	method: "GET",
	url: "/project2/Browse",
	success: (resultData) => handleSearchResult(resultData)
});