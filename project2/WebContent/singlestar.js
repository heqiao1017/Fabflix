function splitListItems(listItems) {
	var array = listItems.split(',');
	console.log(array)
	return array;
}

function handleStarResult() {
	var resultDataString = localStorage.getItem('singleStar');
	console.log(resultDataString);
	resultDataJson = JSON.parse(resultDataString);
	var movieElement = jQuery("#singlestar_table");
	var rowHTML = "";
	rowHTML += "<tr><th>Name:</th>";
	rowHTML += "<th>" + resultDataJson[0]["star_name"] + "</th></tr>";
	rowHTML += "<tr><th>BirthYear:</th>";
	if (resultDataJson[0]["star_birth"]!=null) {
		rowHTML += "<th>" + resultDataJson[0]["star_birth"] + "</th></tr>";
	}
//	rowHTML += "<th>" + resultDataJson[0]["star_birth"] + "</th></tr>";
	rowHTML += "<tr><th style='vertical-align: top;'>Movies:</th>";
//	rowHTML += "<th>" + resultDataJson["movie_title"] + "</th></tr>";
	rowHTML += "<th>";
	var movie_arr = splitListItems(resultDataJson[0]["movie_title"]);
	for (var j = 0; j < movie_arr.length; j++) {
		//rowHTML += "<form id='movie_form"+j+"' action=\"/project2/singlemovie.html\" method=\"get\"><input type=\"hidden\" value=\""+movie_arr[j]+"\" name=\"title\"><a href=\"#\" onclick=\"document.getElementById('movie_form"+j+"').submit();\">"+movie_arr[j]+"</a></form>"
		rowHTML += "<div><a href=\"#\" onclick=\"displaySingleMovie(this, '"+movie_arr[j]+"')\">"+movie_arr[j]+"</a></div>";
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

function displaySingleMovie(id, title) {
	jQuery.ajax({
		"method": "GET",
		"dataType": "json",
		"url": "/project2/SingleMovie?title=" + title,
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
    //id.innerHTML = "Ooops!";
}

//jQuery.ajax({
//	data: getQueryString(),
//	dataType: "json",
//	method: "GET",
//	url: "/project2/singleStar",
//	success: (resultData) => handleStarResult(resultData)
//});
