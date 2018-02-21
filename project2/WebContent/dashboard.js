//handle inserting star
function handleInsertStarResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));

	console.log(resultDataJson);

	jQuery("#insert_message").text(resultDataJson["message"]);
}


function insertStarForm(formSubmitEvent) {
	console.log("submit insert star form");
	formSubmitEvent.preventDefault();
		
	jQuery.post(
		"/project2/DashboardActivity", 
		jQuery("#insert-star").serialize(),
		(resultDataString) => handleInsertStarResult(resultDataString)
	);

}

jQuery("#insert-star").submit((event) => insertStarForm(event));

//======================================================================================================

//showing meta data
function handleMetadataResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));

	console.log(resultDataJson);
	var rowHTML = "";
	for (var i = 0; i < resultDataJson.length; i++) {
		rowHTML += "<table class='metaable'><tr><th colspan='2' class='metatabletr'>" + resultDataJson[i][0]["table_name"] + "</th></tr>";
		for (var j = 0; j < resultDataJson[i].length; j++){
			rowHTML += "<tr>";
			rowHTML += "<th class='metatableth'>" + resultDataJson[i][j]["table_field"] + "</th>";
			rowHTML += "<th class='metatableth'>" + resultDataJson[i][j]["table_type"] + "</th></tr>";
		}
		rowHTML += "</table>";
	}
	jQuery("#metadata_content").append(rowHTML);
}


function metadataForm(formSubmitEvent) {
	console.log("submit meta data form");
	formSubmitEvent.preventDefault();
		
	jQuery.post(
		"/project2/DashboardActivity", 
		jQuery("#metadata-form").serialize(),
		(resultDataString) => handleMetadataResult(resultDataString)
	);

}

jQuery("#metadata-form").submit((event) => metadataForm(event));


//======================================================================================================
function handleAddMovieResult(resultDataString) {
	console.log(resultDataString);
	resultDataJson = JSON.parse(JSON.stringify(resultDataString));

	console.log(resultDataJson);

	jQuery("#addMovie_message").text(resultDataJson["message"]);
}


function addMovieForm(formSubmitEvent) {
	console.log("submit insert star form");
	formSubmitEvent.preventDefault();
		
	jQuery.post(
		"/project2/DashboardActivity", 
		jQuery("#add-movie").serialize(),
		(resultDataString) => handleAddMovieResult(resultDataString)
	);

}

jQuery("#add-movie").submit((event) => addMovieForm(event));