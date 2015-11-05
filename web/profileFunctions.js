var collegeCodeArray = [];
var collegeNameArray = [];
var majorArray = [];
var numColleges = 10;
var numMajors = [];
var collegeOfStudent = "";

var requirements = [];
var coursesTaken = [];
var coursesInProgress = [];
var coursesWithdrew = [];
var coursesNotCompleted = [];
var coursesTransfer =[];

function loadInfo(){
    
    for (i = 0; i < numColleges; i++)
    {
        collegeCodeArray.push("CUNY" + i);
        collegeNameArray.push("College " + i);
        majorArray[i] = [];
        numMajors.push(2 * i + 20);
        for (k = 0; k < numMajors[i]; k++)
        {
            majorArray[i].push("Major " + i + "." + k);
        }
    }
}


function populateColleges(){    
    
    var newOpt = "";
    var defaultSelect = "<option value = \"--\"> -select- </option>";
    
    for (i = 0; i < numColleges; i++)
    {
        newOpt += "<option value = \"" + collegeCodeArray[i] + "\">" +
                collegeNameArray[i] + "</option>";
    }
    document.getElementById("colleges").innerHTML = defaultSelect + newOpt;
}


function populateMajors(){
    var newOpt = "";
    var defaultSelect = "<option value = \"--\"> -select- </option>";
    selectedCollegeCode = document.getElementById("colleges").options[document.getElementById("colleges").selectedIndex].value;
    selectedCollegeIndex = collegeCodeArray.indexOf(selectedCollegeCode);
    
    for (i = 0; i < numMajors[selectedCollegeIndex]; i++)
    {
        newOpt += "<option value = \"" + majorArray[selectedCollegeIndex][i] + "\">" +
                majorArray[selectedCollegeIndex][i] + "</option>";
    }
    document.getElementById("majors").innerHTML = defaultSelect + newOpt;
    
}
    

        function isAClass(input){
            var inputSplit = input.split(" ");
            if(inputSplit.length < 2  || input == "Sublist:"){
                return false;
            }
            if(inputSplit[0] === inputSplit[0].toUpperCase()){
                return true;
            }
            return false;
        }

        function putIntoArrays(lines){
            if(lines.indexOf("Taken") > -1 || lines.indexOf("In-Progress") > -1 || lines.indexOf("Withdrawal") > -1 || lines.indexOf("Not-Completed") > -1 || lines.indexOf("Transfer") > -1 ){
                    return false;
            } 
           return true;
        }

        function populateCourseRequirements(inputString){

            var creditsOrClassNeeded = "";
            var courses = [];
            var index = 0;
            var name = "";
            var credReq = "";
            var credApp = "";
            var isCoursesTaken = false;
            var isCouresTransfer = false;
            var isCoursesInProgress = false;
            var isCoursesNotCompleted = false;
            var isCoursesWithdrawal = false;
            var counter = 0;


            var lines = inputString.split("\n");
            for (var i = 0; i < lines.length; i++){
                if(lines[i].indexOf("College") > -1){
                    collegeOfStudent = lines[i];
                } else if(lines[i].indexOf("in:") >-1) {
                    if(courses.length > 0){
                  
                        var reqJson = {
                            credOrClassNeeded: creditsOrClassNeeded,
                            reqCourses: courses

                        }
                        
                        requirements[index].push(reqJson);
                        courses = [];
                        creditsOrClassNeeded = "";

                    }
                    creditsOrClassNeeded = lines[i];
                } else if(lines[i].indexOf("Choose") >-1){
                    if(courses.length > 0){
                        var reqJson = {
                            credOrClassNeeded: creditsOrClassNeeded,
                            reqCourses: courses
                        }
                       
                        requirements[index].push(reqJson);
                        creditsOrClassNeeded = "";
                        
                        courses = [];
                    }

                    if(i != 0 && lines[i -1] != "or"){
                        requirements[index].push("[".concat(lines[i]));
                        
                    } else if(i != 0 && lines[i - 1] == "or"){
                        requirements[index].push(lines[i]);

                    }
                } else if(lines[i] == "or" || lines[i] == "and"){
                        var reqJson = {
                            credOrClassNeeded: creditsOrClassNeeded,
                            reqCourses: courses
                        }

                        requirements[index].push(reqJson);
                        if(lines[i] == "and"){
                            requirements[index].push("&");
                        }
                        courses = [];                
                        creditsOrClassNeeded = "";
                        counter = 0;

                } else if(lines[i].indexOf("*") >-1){
                    counter++;
                } else if(isAClass(lines[i]) && !isCoursesTaken && !isCoursesInProgress && !isCoursesNotCompleted && !isCouresTransfer && !isCoursesWithdrawal){
                    var temp = lines[i].replace("or", "");
                    temp = temp.replace("OR", "");
                    temp = temp.replace(")", "");
                    temp = temp.replace("and", "");
                    courses.push(temp);
                } else if(lines[i].indexOf("~") > -1){
                    index++; 
                } else if(isCoursesTaken && putIntoArrays(lines[i])) {

                    coursesTaken.push(lines[i]);
                } else if(isCoursesInProgress && putIntoArrays(lines[i])){
                    coursesInProgress.push(lines[i]);
                } else if(isCoursesWithdrawal && putIntoArrays(lines[i])){
                    coursesWithdrew.push(lines[i]);
                } else if(isCoursesNotCompleted && putIntoArrays(lines[i])){
                    coursesNotCompleted.push(lines[i]);
                } else if(isCouresTransfer && putIntoArrays(lines[i])){
                    coursesTransfer.push(lines[i]);
                }

                if(counter >= 2){
                    //if(i != 0 && lines[i - 1].indexOf("*") > -1){

                    var reqJson = {
                    credOrClassNeeded: creditsOrClassNeeded, 
                    reqCourses: courses

                    }
                    requirements[index].push(reqJson);
                    requirements[index].push("]");
                    creditsOrClassNeeded = "";
                    courses = [];
                    counter = 0;
                    //}
                } 

                if(lines[i].indexOf("Taken") > -1){
                    isCoursesTaken = true;
                } else if(lines[i].indexOf("In-Progress") > -1){
                    isCoursesTaken = false;
                    isCoursesInProgress = true;
                } else if(lines[i].indexOf("Withdrawal") > -1){ 
                    isCoursesInProgress = false;
                    isCoursesTaken = false;
                    isCoursesWithdrawal = true;
                } else if(lines[i].indexOf("Not-Completed") > -1){
                    isCoursesInProgress = false;
                    isCoursesTaken = false;
                    isCoursesWithdrawal = false;
                    isCoursesNotCompleted = true;
                } else if(lines[i].indexOf("Transfer") > -1){
                    isCoursesInProgress = false;
                    isCoursesTaken = false;
                    isCoursesWithdrawal = false;
                    isCoursesNotCompleted = false;
                    isCouresTransfer = true;
                } else {

                    if(lines[i].indexOf("Required") > -1){
                        credReq = lines[i];
                    } else if(lines[i].indexOf("Applied") > -1){
                        credApp = lines[i];
                        var reqCat = {
                            name: name, 
                            credReq: credReq, 
                            credApp: credApp
                        }
                        requirements.push([reqCat]);

                    } else {

                        name = lines[i];

                    }
                }
            }
        }


function collegeSelected(){
    document.getElementById("major_wrapper").style.display = "block";
    populateMajors();
}

function globalDbErrorHandler(event){
    window.alert("Error has occurred " + event.target.errorCode);
}

function createDbObject(evt){
    var options = {keyPath: "id", autoIncrement: true};
    var storage = evt.createObjectStore("gracefulTable", options);
    //var indexOptions = {unique: false};
    //storage.createIndex("username", "ciUsername", indexOptions);

}


function store(){
    /*var getColleges = document.getElementById("colleges");
    var theCollege = getColleges.options[getColleges.selectedIndex].text;*/
    //var username = document.getElementById("username").value;
    //var email = document.getElementById("email").value;
    /*var major = document.getElementById(theCollege).value;
    var college = document.getElementById("colleges").value;
    var uncheck = storeClassesNeeded();*/


    var input = {
        courses: requirements
    }

    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");

    var request = store.add(input);

    request.onsuccess = function(){
        window.alert("done adding");
    }
}

function update(id){
    var myArr = {};
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var items = transaction.objectStore("gracefulTable");
    var request = items.get(id);
    request.onsuccess = function(){
        var info = request.result;
        info.courses = myArr;
        items.put(info);
    }

}

function deleteACourse(id, courseId){
    var updatedCourses = {};
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var items = transaction.objectStore("gracefulTable");
    var request = items.get(id);
    request.onsuccess = function(){
        var info = request.result;
        updatedCourses = info.courses;
        delete updatedCourses[courseId];
        info.courses = updatedCourses;
        items.put(info);
    }
}





var db;
window.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

if (window.indexedDB){
    var request = window.indexedDB.open("GracefulDb", 6);
    request.onerror = function(event){
        window.alert("Error creating database");
    };
    request.onsuccess = function(event){
        db = request.result;

    };
    //runs only when version is upgraded
    request.onupgradeneeded = function(event){
        createDbObject(event.target.result);
        //var db = event.target.result;
        //db.deleteObjectStore("myTable");
    };

} else {
    window.alert("Sorry, your browser does not support IndexedDB");
    //ADD REGULAR LOCAL STORAGE HERE
}




/********************************************/
// Server communication
/********************************************/

var xhr;
function testClick(){
    $("#testsubmission").html("Processing...");
    xhr = new XMLHttpRequest();
    xhr.open("GET", "parseprofilepdf?fname=" + $("#fileName").val(), true);
    xhr.onreadystatechange = callback;
    xhr.send();
    $("#footer").html($("#fileName").val());
    
    
}

function callback() {
    if (xhr.readyState == 4){// && xhr.status == 200){    
        // File(s) uploaded.
        //var xmlDoc = xhr.responseXML;
        //var x = xmlDoc.getElementsByTagName("child")[0];
        //var teststring = x.childNodes[0];
        var outputString = xhr.responseText.replace(/(\r\n|\n|\r)/gm, "<br>");
        $("#testsubmission").html(outputString);
            
    }
};




function uploadPDF() {
    var form = document.getElementById('file-form');
    var fileSelect = document.getElementById('file-select');
    var uploadButton = document.getElementById('upload-button');
    
    // Get the selected files from the input.
    
    var selectedFile = fileSelect.files[0];
    // Create a new FormData object.
    
    var formData = new FormData();
    // Add the file to the request.
    formData.append('pdffile', selectedFile, selectedFile.name);

    // Set up the request.
    $.ajax({
        type: "POST",
        url: "parseprofilepdf",
        //enctype: "multipart/form-data",
        data: formData,
        processData: false,
        contentType: false,
        success: function(data){
            $("#testsubmission").html(outputString);
            var outputString = data.replace(/(\r\n|\n|\r)/gm, "\n");
            
            populateCourseRequirements(outputString);
            $("#footer").html("helllooo");
            store();
            
            var outputString = data.replace(/(\r\n|\n|\r)/gm, "<br>");
            $("#testsubmission").html(outputString);
            
            //store(outputString);
            //store(outputString);
        }
            
    });
}


/********************************************/
// Retrieving profile information for display
/********************************************/
//Currently test data:


