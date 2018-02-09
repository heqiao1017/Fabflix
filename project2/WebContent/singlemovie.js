//generate the single movie page data
function splitListItems(listItems) {
	var array = listItems.split(',');
	return array;
}


function getQueryString() {
	var result = {}, queryString = location.search.slice(1), re = /([^&=]+)=([^&]*)/g, m;
	
	while (m = re.exec(queryString)) {
		result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
	}
	return result;
}

function handleMovieResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	var movieElement = jQuery("#singlemovie_table");
	var rowHTML = "";
	rowHTML += "<tr><th>Id:</th>";
	rowHTML += "<th>" + resultDataJson[0]["movie_id"] + "</th></tr>";
	rowHTML += "<tr><th>Title:</th>";
	rowHTML += "<th>" + resultDataJson[0]["movie_title"] + "</th><th><form id='addcart_form' action='/project2/shoppingcart.html\' method='get'><input type='hidden' value='"+resultDataJson[0]["movie_title"]+"' name='title'><a href='#' onClick='addToShoppingCart();'>Add to Cart</a></form></th></tr>";
	rowHTML += "<tr><th>Year:</th>";
	rowHTML += "<th>" + resultDataJson[0]["movie_year"] + "</th></tr>";
	rowHTML += "<tr><th>Director:</th>";
	rowHTML += "<th>" + resultDataJson[0]["movie_director"] + "</th></tr>";
	
	rowHTML += "<tr style='vertical-align: top;'><th>Genres:</th>";
	
	rowHTML += "<th>";
	var genre_arr = splitListItems(resultDataJson[0]["movie_genres"]);
	for (var j = 0; j < genre_arr.length; j++) {
		rowHTML += "<form id='genre_form"+j+"' action=\"/project2/movielist.html\" method=\"get\"><input type=\"hidden\" value=\""+genre_arr[j]+"\" name=\"genre\"><a href=\"#\" onclick=\"document.getElementById('genre_form"+j+"').submit();\">"+genre_arr[j]+"</a></form>"
	}
	rowHTML += "</th></tr>";
	
	rowHTML += "<tr style='vertical-align: top;'><th>Stars:</th>";
	
		
	rowHTML += "<th>";
	var star_arr = splitListItems(resultDataJson[0]["movie_stars"]);
	for (var j = 0; j < star_arr.length; j++) {
		rowHTML += "<form id='star_form"+j+"' action=\"/project2/singlestar.html\" method=\"get\"><input type=\"hidden\" value=\""+star_arr[j]+"\" name=\"star\"><a href=\"#\" onclick=\"document.getElementById('star_form"+j+"').submit();\">"+star_arr[j]+"</a></form>"
	}
	rowHTML += "</th></tr>";
	
	rowHTML += "</tr>"
	movieElement.append(rowHTML);
}

jQuery.ajax({
	data: getQueryString(),
	dataType: "json",
	method: "GET",
	url: "/project2/SingleMovie",
	success: (resultData) => handleMovieResult(resultData)
});

function addToShoppingCart() {
	document.getElementById("addcart_form").submit();
}




	