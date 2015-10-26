var collegeCodeArray = [];
var collegeNameArray = [];
var majorArray = [];
var numColleges = 10;
var numMajors = [];

var selectedCollege;
var selectedMajor;


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


function populateCourseRequirements(){

    
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

function myFunc(id){

    var getChoices = document.getElementById(id);
    var chosen = getChoices.options[getChoices.selectedIndex].text;
    document.getElementById("coursesTaken").style.display = "block";

    switch(chosen){
        case "Accounting":
            document.getElementById("accounting").style.display = "block";
            document.getElementById("finance").style.display = "block";
            break;


    }

    //var accountingElective = ["ECO 26000", "ECO 48000", "Any 300 - or 400 - level ECO course", "one comp sci class may be used as elective"]; //12 credits
}

function storeClassesNeeded(){
    var getColleges = document.getElementById("colleges");
    var theCollege = getColleges.options[getColleges.selectedIndex].text;
    var getChoices = document.getElementById(theCollege);
    var chosen = getChoices.options[getChoices.selectedIndex].text;
    var uncheck = {};

    switch(chosen){
        case "Accounting":
            var classes = document.getElementById("accounting").getElementsByTagName("INPUT");

            var classes2 = document.getElementById("finance").getElementsByTagName("input");
            var index = 0;
            for (var i = 0; i < classes.length; i++){
                if(!classes[i].checked){
                    /*uncheck[index] = classes[i].value;
                    index++;*/
                    uncheck[classes[i].value] = classes[i].value;
                    index++;
                } 
            }					
            break;
    }
    return uncheck;
}

function store(){
    var getColleges = document.getElementById("colleges");
    var theCollege = getColleges.options[getColleges.selectedIndex].text;
    var username = document.getElementById("username").value;
    var email = document.getElementById("email").value;
    var major = document.getElementById(theCollege).value;
    var college = document.getElementById("colleges").value;
    var uncheck = storeClassesNeeded();


    var input = {
        username: username,
        email: email,
        college: college,
        major: major,
        courses: uncheck
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


