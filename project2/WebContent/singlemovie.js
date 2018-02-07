function splitListItems(listItems) {
	var array = listItems.split(',');
	console.log(array)
	return array;
}

function handleStarResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	var movieElement = jQuery("#singlestar_table");
	var rowHTML = "";
	rowHTML += "<tr><th>Name:</th>";
	rowHTML += "<th>" + resultDataJson[0]["star_name"] + "</th></tr>";
	rowHTML += "<tr><th>BirthYear:</th>";
	rowHTML += "<th>" + resultDataJson[0]["star_birth"] + "</th></tr>";
	rowHTML += "<tr><th style='vertical-align: top;'>Movies:</th>";
//	rowHTML += "<th>" + resultDataJson["movie_title"] + "</th></tr>";
	rowHTML += "<th>";
	var movie_arr = splitListItems(resultDataJson[0]["movie_title"]);
	for (var j = 0; j < movie_arr.length; j++) {
		rowHTML += "<form id='movie_form"+j+"' action=\"/project2/singlemovie.html\" method=\"get\"><input type=\"hidden\" value=\""+movie_arr[j]+"\" name=\"title\"><a href=\"#\" onclick=\"document.getElementById('movie_form"+j+"').submit();\">"+movie_arr[j]+"</a></form>"
	}
	rowHTML += "</th></tr>";
	
	rowHTML += "</tr>"
	movieElement.append(rowHTML);
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
	url: "/project2/singleStar",
	success: (resultData) => handleStarResult(resultData)
});




//$(document).ready(function(){
////	var myParam = getQueryString()["title"];
////	console.log(getQueryString());
//	jQuery.ajax({
//		data: getQueryString(),
//		dataType: "json",
//		method: "GET",
//		url: "/project2/singleStar",
//		success: (resultData) => handleStarResult(resultData)
//	});
//});
