/**
 * 
 */

function getQueryString() {
	var result = {}, queryString = location.search.slice(1), re = /([^&=]+)=([^&]*)/g, m;
	
	while (m = re.exec(queryString)) {
		result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
	}
	return result;
}

function handleShoppingCartResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	
	
	var cartTableBodyElement = jQuery("#cart_table_body");
	var cartTableHeadElement = jQuery("#cart_head");
	if (jQuery.isEmptyObject(resultDataJson)) {
		var rowHTML = "<tr><td><h2>No Item</h2></td></tr>";
		cartTableHeadElement.append(rowHTML);
	} else {
		var rowHTML = "<tr><th>Movie</th><th>Quantity</th></tr>";
		cartTableHeadElement.append(rowHTML);
		for (var i = 0; i < resultDataJson.length; i++) {
			var rowHTML = "";
			rowHTML += "<tr>";
			rowHTML += "<th>" + resultDataJson[i]["movie_title"] + "</th>";
			
			//rowHTML += "<th>" + resultDataJson[i]["movie_quantity"] + "</th>";
			rowHTML += "<th><form id='update_form"+i+"' action='/project2/shoppingcart.html' method='get'><input type='hidden' value='"+resultDataJson[i]["movie_title"]+"' name='title'><input class='input-background' value='"+resultDataJson[i]["movie_quantity"]+"' name='qty' id='qty_input"+i+"'><input class=\"btn btn-outline-success\" type ='submit' form='update_form"+i+"' value='Update'><button class=\"btn btn-outline-success\" id='delete_button"+i+"' onclick=\"deleteOnClicked();\">Delete</button></form></th>";//<input type ='submit' form='update_form"+i+"' value='Delete'>
			
			rowHTML += "</tr>";
			cartTableBodyElement.append(rowHTML);
		}
		cartTableBodyElement.append("<form action='/project2/checkout.html' method='get'><input class=\"btn btn-outline-success\" type ='submit' value='Proceed to Checkout'></form>");
	}
	
}

function deleteOnClicked() {
	$('input[name="qty"]').val('0')
}

jQuery.ajax({
	data: getQueryString(),
	dataType: "json",
	method: "GET",
	url: "/project2/ShoppingCart",
	success: (resultData) => handleShoppingCartResult(resultData)
});

//onClick='updateButtonClicked();'
//function changeValue(o){
//    document.getElementById('qty_input').value=o.innerHTML;
// }
//
//<input type='hidden' value='"+getQTYValue()+"' name='qty_update'>
//function getQTYValue(){
//	var id = "qty_input";
//	if (document.getElementById(id) != null) {
//		return document.getElementById(id).value;
//	}
//	return null;
//}



function empty() {
	var id = "qty_input"+movieId;
    var qty_input = document.getElementById(id).value;
    if (!jQuery.isNumeric(qty_input)) {
        alert("Invalid input!");
        return false;
    };
}