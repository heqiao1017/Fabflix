//generate the single movie page data
function splitListItems(listItems) {
	var array = listItems.split(',');
	return array;
}

var resultDataString;
function getQueryString() {
	var result = {}, queryString = location.search.slice(1), re = /([^&=]+)=([^&]*)/g, m;
	
	while (m = re.exec(queryString)) {
		result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
	}
	return result;
}
//function handleMovieResult(resultDataString)
function handleMovieResult() {
	resultDataString = localStorage.getItem('singleMovie');
	console.log(resultDataString);
	resultDataJson = JSON.parse(resultDataString);
	var movieElement = jQuery("#singlemovie_table");
	var rowHTML = "";
	rowHTML += "<div class=\"col-xs-12\">"
	rowHTML += "<div class='col-xs-6 col-sm-4'>Id:</div>";
	rowHTML += "<div class='col-xs-6 col-sm-8'>" + resultDataJson[0]["movie_id"] + "</div>";
	rowHTML += "</div>"
	rowHTML += "<div class=\"col-xs-12\">"
	rowHTML += "<div class='col-xs-6 col-sm-4'>Title:</div>";
	rowHTML += "<div class='col-xs-6 col-sm-8'>" + resultDataJson[0]["movie_title"] + "</div>";//</th><th><form id='addcart_form' action='/project2/shoppingcart.html\' method='get'><input type='hidden' value='"+resultDataJson[0]["movie_title"]+"' name='title'><a href='#' onClick='addToShoppingCart();'>Add to Cart</a></form>
	rowHTML += "</div>"
	rowHTML += "<div class=\"col-xs-12\">"
	rowHTML += "<div class='col-xs-6 col-sm-4'>Year:</div>";
	rowHTML += "<div class='col-xs-6 col-sm-8'>" + resultDataJson[0]["movie_year"] + "</div>";
	rowHTML += "</div>"
	rowHTML += "<div class=\"col-xs-12\">"
	rowHTML += "<div class='col-xs-6 col-sm-4'>Director:</div>";
	rowHTML += "<div class='col-xs-6 col-sm-8'>" + resultDataJson[0]["movie_director"] + "</div>";
	rowHTML += "</div>"
	
	rowHTML += "<div class=\"col-xs-12\">"
	rowHTML += "<div class='col-xs-6 col-sm-4' style='vertical-align: top;'>Genres:</div>";
	rowHTML += "<div class='col-xs-6 col-sm-8'>";
	var genre_arr = splitListItems(resultDataJson[0]["movie_genres"]);
	for (var j = 0; j < genre_arr.length; j++) {
		//rowHTML += "<form id='genre_form"+j+"' action=\"/project2/movielist.html\" method=\"get\"><input type=\"hidden\" value=\""+genre_arr[j]+"\" name=\"genre\"><a href=\"#\" onclick=\"document.getElementById('genre_form"+j+"').submit();\">"+genre_arr[j]+"</a></form>"
		//<button onclick="displayDate()">The time is?</button>
		rowHTML += "<div><a href=\"#\" onclick=\"displayMovies(this, '"+genre_arr[j]+"')\">"+genre_arr[j]+"</a></div>";
	}
	rowHTML += "</div>";
	rowHTML += "</div>"
	
	
	rowHTML += "<div class=\"col-xs-12\">"
	rowHTML += "<div class='col-xs-6 col-sm-4' style='vertical-align: top;'>Stars:</div>";
	rowHTML += "<div class='col-xs-6 col-sm-8'>";
	var star_arr = splitListItems(resultDataJson[0]["movie_stars"]);
	for (var j = 0; j < star_arr.length; j++) {
		//rowHTML += "<form id='star_form"+j+"' action=\"/project2/singlestar.html\" method=\"get\"><input type=\"hidden\" value=\""+star_arr[j]+"\" name=\"star\"><a href=\"#\" onclick=\"document.getElementById('star_form"+j+"').submit();\">"+star_arr[j]+"</a></form>"
		rowHTML += "<div><a href=\"#\" onclick=\"displayStar(this, '"+star_arr[j]+"')\">"+star_arr[j]+"</a></div>";
	}
	rowHTML += "</div>";
	rowHTML += "</div>"
	
	rowHTML += "<div class='col-xs-12' style='padding:10px;'></div><div class='col-xs-6 col-sm-4'></div><div class='col-xs-6 col-sm-8'><form id='addcart_form' class='checkout-btn' action='/project2/shoppingcart.html\' method='get'><input type='hidden' class='checkout-link' value='"+resultDataJson[0]["movie_title"]+"' name='title'><a href='#' style='text-decoration:none;' onClick='addToShoppingCart();'>Add to Cart</a></form></div>";
	
	movieElement.append(rowHTML);
}

function displayStar(id, star) {
	jQuery.ajax({
		"method": "GET",
		"dataType": "json",
		"url": "/project2/singleStar?star=" + star,
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
    //id.innerHTML = "Ooops!";
}

function displayMovies(id, genre) {
	jQuery.ajax({
		"method": "GET",
		"dataType": "json",
		"url": "/project2/Search?genre=" + genre,
		"success": function(resultDataString) {
			//console.log(resultDataString);
			localStorage.setItem('myMainKey', JSON.stringify(resultDataString));
			window.location.href = "/project2/movielist.html";
		},
		"error": function(errorData) {
			console.log("displayMovies ajax error")
			console.log(errorData)
		}
	})
    //id.innerHTML = "Ooops!";
}

//jQuery.ajax({
//	data: getQueryString(),
//	dataType: "json",
//	method: "GET",
//	url: "/project2/SingleMovie",
//	success: (resultData) => handleMovieResult(resultData)
//});

function addToShoppingCart() {
	document.getElementById("addcart_form").submit();
}




	