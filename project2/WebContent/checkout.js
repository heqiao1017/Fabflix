function getQueryString() {
	var result = {}, queryString = location.search.slice(1), re = /([^&=]+)=([^&]*)/g, m;
	
	while (m = re.exec(queryString)) {
		result[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
	}
	return result;
}

function handleCheckoutResult(resultDataString) {
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));
	
	console.log("handle handleCheckoutResult response");
	console.log(resultDataJson);
	
	var confirmationBodyElement = jQuery("#confirmation_body");
	var confirmationHeadElement = jQuery("#confirmation_head");
	if (jQuery.isEmptyObject(resultDataJson)) {
		var rowHTML = "<tr><td><h2>Fail! Wrong customer information!</h2></td></tr>";
		confirmationHeadElement.append(rowHTML);
	}
	else {
		var rowHTML = "<tr><td><h2>Order Confirmation</h2></td></tr>";
		rowHTML += "<tr><th>Movie Title</th><th>Quantity</th><th>Transaction ID</th><th>Username</th><th>Sale Date</th></tr>";
		confirmationHeadElement.append(rowHTML);
		
		for (var i = 0; i < resultDataJson.length; i++) {
			var rowHTML ="";
			rowHTML += "<tr>";
			rowHTML += "<th>" + resultDataJson[i]["movie_title"] + "</th>";
			rowHTML += "<th>" + resultDataJson[i]["movie_qty"] + "</th>";
			rowHTML += "<th>" + resultDataJson[i]["transaction_id"] + "</th>";
			rowHTML += "<th>" + resultDataJson[i]["customer_email"] + "</th>";
			rowHTML += "<th>" + resultDataJson[i]["sale_date"] + "</th></tr>";
			
			confirmationBodyElement.append(rowHTML);
		}
		
	}
}


jQuery.ajax({
	  data: getQueryString(),
	  dataType: "json",
	  method: "GET",
	  url: "/project2/Checkout",
	  success: (resultData) => handleCheckoutResult(resultData)
});