
var movieTableBodyElement = jQuery("#movie_table_body");
var movieTableHeadElement = jQuery("#movie_head");
var totalJsonSize, curItemsPerPage;
var curPage = 0;

function splitListItems(listItems) {
	console.log(listItems)
	var array = listItems.split(',');
	return array;
}

function handleSearchResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	totalJsonSize = resultDataJson.length;
	
	console.log("handle search response");
	console.log(resultDataJson);
	//will always direct to movieList.html page, but can be "No Result Found" or movie list

	var curItemsPerPage = $("#search-count option:selected").val();
	
	if (jQuery.isEmptyObject(resultDataJson)) {
		var rowHTML = "<tr><td><h2>No Result Found</h2></td></tr>";
		movieTableHeadElement.append(rowHTML);
	}
	else {
		var rowHTML = "<tr><th>ID</th><th onclick=\"sortTable(1)\"><a href=\"#\">Title</a></th><th onclick=\"sortTable(2)\"><a href=\"#\">Year</a></th><th>Director</th><th>Genres</th><th>Stars</th></tr>";
		movieTableHeadElement.append(rowHTML);
		
		// show items per page in html
		showPage(curItemsPerPage);
	}	
}




function showPage(itemsPerPage) {
	$( "#movie_table_body" ).empty();
	curItemsPerPage = itemsPerPage;
	var rowHTML = "";
	for (var i = curPage * curItemsPerPage, k = 0; k < curItemsPerPage && i < totalJsonSize; k++, i++) {
		rowHTML = "";
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
		rowHTML += "</tr>"
		movieTableBodyElement.append(rowHTML);
		
	}
}


function prevPage() {
	if (curPage > 0) {
		curPage--;
	}
	showPage(curItemsPerPage);
}

function nextPage() {
	if ((curPage + 1) * curItemsPerPage < totalJsonSize) {
		curPage++;
	}
	showPage(curItemsPerPage) ;
}


function getQueryString() {
	var result = {}, queryString = location.search.slice(1), re = /([^&=]+)=([^&]*)/g, m;
	
	while (m = re.exec(queryString)) {
		result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
	}
	return result;
}


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

function sortTable(n) {
	  var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
	  table = document.getElementById("movie_table");
	  switching = true;
	  //Set the sorting direction to ascending:
	  dir = "asc"; 
	  /*Make a loop that will continue until
	  no switching has been done:*/
	  while (switching) {
	    //start by saying: no switching is done:
	    switching = false;
	    rows = table.getElementsByTagName("TR");
	    /*Loop through all table rows (except the
	    first, which contains table headers):*/
	    for (i = 1; i < (rows.length - 1); i++) {
	      //start by saying there should be no switching:
	      shouldSwitch = false;
	      /*Get the two elements you want to compare,
	      one from current row and one from the next:*/
	      x = rows[i].getElementsByTagName("TH")[n];
	      y = rows[i + 1].getElementsByTagName("TH")[n];
	      /*check if the two rows should switch place,
	      based on the direction, asc or desc:*/
	      if (dir == "asc") {
	        if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
	          //if so, mark as a switch and break the loop:
	          shouldSwitch= true;
	          break;
	        }
	      } else if (dir == "desc") {
	        if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
	          //if so, mark as a switch and break the loop:
	          shouldSwitch= true;
	          break;
	        }
	      }
	    }
	    if (shouldSwitch) {
	      /*If a switch has been marked, make the switch
	      and mark that a switch has been done:*/
	      rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
	      switching = true;
	      //Each time a switch is done, increase this count by 1:
	      switchcount ++;      
	    } else {
	      /*If no switching has been done AND the direction is "asc",
	      set the direction to "desc" and run the while loop again.*/
	      if (switchcount == 0 && dir == "asc") {
	        dir = "desc";
	        switching = true;
	      }
	    }
	  }
}


jQuery.ajax({
	  data: getQueryString(),
	  dataType: "json",
	  method: "GET",
	  url: "/project2/Search",
	  success: (resultData) => handleSearchResult(resultData)
});
