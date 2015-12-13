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
var major = "";
var boolArr = [];
boolArr[0] = false;
var id_num = 0;

var userResume = {
    name: "",
    address: "",
    city: "",
    state: "",
    zip: "",
    phone: "",
    email: "",
    education: [],
    work: [],
    skills: []    
};



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

    
/**
	This function will take an string input and check if it is a course (eg. ENGL 2100).
	It will return a boolean indicating if the string passed into it is a course.

*/
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


/**
	This function will take in a string and decide whether it will put the information following the string into an array.
	It will check whether the input string contiains the word Courses.
	It will return a boolean indicating whether it should put the information following the input string into an array.
*/
function putIntoArrays(lines){
    if(lines.indexOf("Courses") > -1 ){
            return false;
    } 
   return true;
}


/**
	This function will take in a string and parse the information to put it into a jagged array.
*/
function populateCourseRequirements(inputString){
    requirements = [];
    coursesTaken = [];
    coursesInProgress = [];
    coursesWithdrew = [];
    coursesNotCompleted = [];
    coursesTransfer =[];
    major = "";

    var exceptions = [];
    var creditsOrClassNeeded = "";
    var courses = [];
    var index = 0;
    var name = "";
    var credReq = "";
    var credApp = "";
    var isCoursesTaken = false;
    var isCoursesTransfer = false;
    var isCoursesInProgress = false;
    var isCoursesNotCompleted = false;
    var isCoursesWithdrawal = false;
    var counter = 0;
    var containsExceptions = false;

    var lines = inputString.split("\n");
    
    for (var i = 0; i < lines.length; i++){
        if(lines[i].indexOf("College") > -1 && i ==0) {
            collegeOfStudent = lines[i];

        } else if(lines[i].indexOf("Major") > -1 && i == 1){
            var temp = lines[i].replace("Catalog Year:", "");
            major = temp;


        } else if(lines[i].indexOf("in:") >-1) {
            
            if(courses.length > 0){
          		
                var reqJson = {
                    credOrClassNeeded: creditsOrClassNeeded,
                    reqCourses: courses,
                    exceptionClasses: exceptions

                }
                
                requirements[index].push(reqJson);
                courses = [];
                containsExceptions = false;
                exceptions = [];
                creditsOrClassNeeded = "";

            }
            creditsOrClassNeeded = lines[i];
        } else if(lines[i].indexOf("Choose") >-1){
            if(courses.length > 0){
      		
                var reqJson = {
                    credOrClassNeeded: creditsOrClassNeeded,
                    reqCourses: courses,
                    exceptionClasses: exceptions

                }
                
                requirements[index].push(reqJson);
                courses = [];
                containsExceptions = false;
                exceptions = [];
                creditsOrClassNeeded = "";

            }

            if(i != 0 && lines[i -1] != "or"){
                if(lines[i-1].indexOf("Choose") > -1){
                    requirements[index].push(lines[i]);

                } else {
                    requirements[index].push("[".concat(lines[i]));
                }
                
            } else if(i != 0 && lines[i - 1] == "or"){
                requirements[index].push(lines[i]);

            }
        } else if(lines[i].indexOf("~") > -1){
            if(courses.length > 0){
          		
                var reqJson = {
                    credOrClassNeeded: creditsOrClassNeeded,
                    reqCourses: courses,
                    exceptionClasses: exceptions

                }
                
                requirements[index].push(reqJson);
                courses = [];
                containsExceptions = false;
                exceptions = [];
                creditsOrClassNeeded = "";

            }
            index++; 

        } else if(containsExceptions){
            exceptions.push(lines[i]);
        } else if(lines[i].indexOf("won't count") > -1){
            containsExceptions = true;
        } else if(lines[i] == "or" || lines[i] == "and"){
                var reqJson = {
                    credOrClassNeeded: creditsOrClassNeeded,
                    reqCourses: courses, 
                    exceptionClasses: exceptions
                }

                requirements[index].push(reqJson);
                if(lines[i] == "and"){
                    requirements[index].push("&");
                }
                courses = [];                
                creditsOrClassNeeded = "";
                exceptions = [];
                containsExceptions = false;
                counter = 0;

        } else if(lines[i].indexOf("*") >-1){
            counter++;
        } else if(isAClass(lines[i]) && !isCoursesTaken && !isCoursesInProgress && !isCoursesNotCompleted && !isCoursesTransfer && !isCoursesWithdrawal){
            var temp = lines[i].replace("or", "");
            temp = temp.replace("OR", "");
            temp = temp.replace(")", "");
            temp = temp.replace("and", "");
            var temp2 =  temp.split(" ");

            if(temp2[0] == "FALL" || temp2[0] == "SPRING" || temp2[0] == "SUMMER" || temp2[0] == "WINTER"){
                courses[courses.length -1] = courses[courses.length-1].concat(temp);
            } else {
                courses.push(temp);
            }

        } else if(isCoursesTaken && putIntoArrays(lines[i])) {

            coursesTaken.push(lines[i]);
        } else if(isCoursesInProgress && putIntoArrays(lines[i])){
            coursesInProgress.push(lines[i]);
        } else if(isCoursesWithdrawal && putIntoArrays(lines[i])){
            coursesWithdrew.push(lines[i]);
        } else if(isCoursesNotCompleted && putIntoArrays(lines[i])){
            coursesNotCompleted.push(lines[i]);
        } else if(isCoursesTransfer && putIntoArrays(lines[i])){
            coursesTransfer.push(lines[i]);
        }

        if(counter >= 2){

            var reqJson = {
            credOrClassNeeded: creditsOrClassNeeded, 
            reqCourses: courses,
            exceptionClasses: exceptions


            }
            requirements[index].push(reqJson);
            requirements[index].push("]");
            creditsOrClassNeeded = "";
            courses = [];
            exceptions = [];
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
            isCoursesTransfer = true;
        } else {

            if(lines[i].indexOf("Required") > -1){
                credReq = lines[i];
            } else if(lines[i].indexOf("Credits Applied") > -1){

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
	var options = {keyPath: "username"};
	var storage = evt.createObjectStore("gracefulTable", options);
	storage.createIndex("username", "username", {unique: true});
}


/**
	This function will take store course requirement information for the user in cient side.
	It will take in a string containing the which user the information belongs to.
*/
function storeReq(username) {
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var request = store.get(username);
    request.onsuccess = function(){
        var data = request.result;
        data.req = requirements;
        data.coursesInProgress = coursesInProgress;
        data.coursesTaken = coursesTaken;
        data.coursesTransfer = coursesTransfer;
        data.coursesWithdrew = coursesWithdrew;
        data.coursesNC = coursesNotCompleted;
        data.college = collegeOfStudent;
        data.major = major;
        var update = store.put(data);
    }
    
}

var allCourseReq = new Array();

var schedArr = [];


/**
	This function defaults the profile information to empty values after a username is created. 
	The profile information will be populated as the user supplies the information. 
*/
function store(username){    

    var input = {
        username: username,
        college: collegeOfStudent, 
        major: major, 
        coursesInProgress: coursesInProgress,
        coursesTaken: coursesTaken,
        coursesTransfer: coursesTransfer,
        coursesWithdrew: coursesWithdrew,
        coursesNC: coursesNotCompleted,
        sched: schedArr,
        resume: userResume,
        allCourseReq: allCourseReq,
        req: [],
        id_num: id_num
    }

    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");

    var request = store.add(input);

    request.onsuccess = function(){
    }

    request.onerror = function(){
        var transaction = db.transaction(["gracefulTable"], "readwrite");
        var store = transaction.objectStore("gracefulTable");
        var request2 = store.get(username);
        request2.onsuccess = function(){
        }
    }
}


/**
	This function returns the requirement information from client side storage to an array. It will also display the resulting array.
	It takes in the username of the user whose information is being retrieved, and an array to store the information in.

*/
function getIndexForReq(username, arr){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess  = function(){
        var data = req.result;
            arr = data.req;
            displayReq(arr, true);
    }
}

function getReqLength(){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess  = function(){
        var data = req.result;
        return req.result.length;
    }
}

function getCollege(){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(userName);
    req.onsuccess  = function(){
        var data = req.result;
        getCollegeAndMajor(req.result.college, 1);
        
    }
}

function getMajor(){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(userName);
    req.onsuccess  = function(){
        var data = req.result;
        getCollegeAndMajor(req.result.major, 2);
        
    }
}

function getCompletedCourses(){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(userName);
    req.onsuccess  = function(){
        var data = [];
        data = req.result.coursesInProgress.concat(req.result.coursesTaken);
        data = data.concat(req.result.coursesTransfer);
        
        getCoursesTaken(data, true);
    }
}




var db;
window.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

if (window.indexedDB){
    var request = window.indexedDB.open("GracefulDb", 8);
    request.onerror = function(event){
        window.alert("Error creating database");
    };
    request.onsuccess = function(event){
        db = request.result;
        selectUser(false);

    };
    //runs only when version is upgraded
    request.onupgradeneeded = function(event){
        thisdb = event.target.result;

                //createDbObject(thisdb);
        if(thisdb.objectStoreNames.contains("gracefulTable")){
            thisdb.deleteObjectStore("gracefulTable");
        }
        createDbObject(thisdb);

    };

} else {
    window.alert("Sorry, your browser does not support IndexedDB");
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



var array1 = [];
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
            
            var outputString1 = data.replace(/(\r\n|\n|\r)/gm, "\n");
            
            populateCourseRequirements(outputString1);
            
            var outputString = data.replace(/(\r\n|\n|\r)/gm, "<br>");
            //$("#testsubmission").html(outputString);
            
            storeReq(userName);
            //getIndexForReq(userName, 0, array1); 
            //displayReq(array1, false);
            prepProfile();
            //alert(array1[0].name);
        },
        error: function(obj, errType){
            alert("An error has occurred..." + "\n" + "Error: " + errType);
        }
            
    });
}



/*****************************************************/
// SCHEDULE GETS AND SETS
/*****************************************************/

	
function Schedule(dayStart, dayEnd, hourStart, hourEnd, openTimes, closedTimes, classTimes, selectedDivs, valid, selectedCourses){
    this.dayStart = dayStart;
    this.dayEnd = dayEnd;
    this.hourStart = hourStart;
    this.hourEnd = hourEnd;
    this.openTimes = scheduleTabs[currentScheduleTab].openTimes;
    this.closedTimes = scheduleTabs[currentScheduleTab].closedTimes;
    this.selectedDivs = scheduleTabs[currentScheduleTab].selectedDivs;
    this.valid = valid;
    this.classTimes = scheduleTabs[currentScheduleTab].classTimes;
    this.selectedCourses = scheduleTabs[currentScheduleTab].selectedCourses;
}


//FUNCTIONS FOR SCHEDULES 
//IT SHOULD GO INTO THE PROFILEFUNCTIONS.JS SO THE VARIABLE DB IS KNOWN!

function createSched(username){
    var open = new Array();
    var closedTime = new Array();
    var selectedDiv = new Array();
    var classTime = new Array();
    var selectedCourse = new Array();
    var testSched = new Schedule(0,6,0,23,open, closedTime, classTime, selectedDiv, true, selectedCourse);
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var request = store.get(username);
    request.onsuccess = function(){
        var data = request.result;
         data.sched.push(testSched);
         var update = store.put(data);
    }
}


function setDayStart(username, dayStart){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store= transaction.objectStore("gracefulTable");
    var request = store.get(username);
    request.onsuccess = function(){
        var data = request.result;
        data.sched[currentScheduleTab].dayStart = dayStart;
        var update = store.put(data);
    }
}

function setDayEnd(username, dayEnd){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].dayEnd = dayEnd;
        var update = store.put(data);
    }
}

function setHoursStart(username, hoursStart) {
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].hourStart = hoursStart;
        var update = store.put(data);
    }
}

function setHoursEnd(username, hoursEnd) {
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].hourEnd = hoursEnd;
        var update = store.put(data);
    }
}


/*
    This function will set the open time values in indexeddb to the param, openTimes.
    @ param openTimes, An array containing user selected openTime.
*/
function setOpenTimes(username, openTimes){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].openTimes = openTimes;
        var update = store.put(data);
    }
}

function setClosedTimes(username, closeTimes){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].closedTimes = closeTimes;
        var update = store.put(data);
    }
}

function setSelectedDiv(username, selectedDiv){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].selectedDivs = selectedDiv;
        var update = store.put(data);
    }
}

function setSelectedCourses(username, selectedCourses){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].selectedCourses = selectedCourses;
        var update = store.put(data);
    }
}

function setClassTimes(username, classTimes){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].classTimes = classTimes;
        var update = store.put(data);
    }
}

function setValid(username, valid){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    var prevSchedTab = currentScheduleTab;
    req.onsuccess = function(){
        var data = req.result;
        data.sched[prevSchedTab].valid = valid;
        var update = store.put(data);
    }
}

/*
    This function will take an array, arr, by reference populate it with dayStart.
    @ param arr, An empty array to be filled with the dayStart
    @ param boolArr, An array with one element set to either true or false. This will let you know when this function is done.
*/
function getDayStart(username, arr, boolArr){
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        arr.push(data.sched[currentScheduleTab].dayStart);
        boolArr[0] = true;	
    }
}


/*
    This function will take an array by reference populate it with dayEnd.
    @ param arr, An empty array to be filled with the dayEnd.
    @ param boolArr, An array with one element set to either true or false. This will let you know when this function is done.
*/
function getDayEnd(username, arr, boolArr){
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        arr.push(data.sched[currentScheduleTab].dayEnd);
        boolArr[0] = true;	
    }
}


/*
    This function will take an array by reference populate it with hoursStart.
    @ param arr, An empty array to be filled with the hoursStart.
    @ param boolArr, An array with one element set to either true or false. This will let you know when this function is done.
*/
function getHoursStart(username, arr, boolArr){
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        arr.push(data.sched[currentScheduleTab].hoursStart);
        boolArr[0] = true;	
    }
}


/*
    This function will take an array by reference populate it with hoursEnd.
    @ param arr, An empty array to be filled with the hoursEnd.
    @ param boolArr, An array with one element set to either true or false. This will let you know when this function is done.
*/
function getHoursEnd(username, arr, boolArr){
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        arr.push(data.sched[currentScheduleTab].hoursEnd);
        boolArr[0] = true;	
    }
}



/*
    This function will take an array by reference populate it with openTimes.
    @ param arr, An empty array to be filled with the openTimes values,
    @ param boolArr, An array with one element set to either true or false. This will let you know when this function is done.
*/
function getOpenTimes(username, myArr, boolArr) {
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;

        for(var i = 0; i < data.sched[currentScheduleTab].openTimes.length; i++){
            myArr.push(data.sched[currentScheduleTab].openTimes[i]);
        }

        boolArr[0] = true;	
    }
}



/*
    This function will take an array by reference populate it with closeTimes.
    @ param arr, An empty array to be filled with the closeTimes values.
    @ param boolArr, An array with one element set to either true or false. This will let you know when this function is done.
*/
function getClosedTimes(username, myArr, boolArr) {
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;

        for(var i = 0; i < data.sched[currentScheduleTab].closedTimes.length; i++){
            myArr.push(data.sched[currentScheduleTab].openTimes[i]);
        }

        boolArr[0] = true;	
    }
}	

/*
    This function will take an array by reference populate it with selectedDiv.
    @ param arr, An empty array to be filled with the selectedDiv values.
    @ param boolArr, An array with one element set to either true or false. This will let you know when this function is done.
*/
function getSelectedDiv(username, myArr, boolArr) {
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;

        for(var i = 0; i < data.sched[currentScheduleTab].selectedDivs.length; i++){
            myArr.push(data.sched[currentScheduleTab].openTimes[i]);
        }

        boolArr[0] = true;	
    }
}


function getSchedules(username, myArr, boolArr) {
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        
        //myArr = data.sched;
        scheduleTabs = data.sched;
        //scheduleObjArray = data.sched;
        //alert(scheduleObjArray[0].openTimes);
        /*
        for(var i = 0; i < data.sched.length; i++){
            myArr.push(data.sched[i]);
        }
        */
        boolArr[0] = true;
        loadUserSchedules(true);
    }
}

function getValid(username, arr, boolArr){
    boolArr[0] = false;
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        arr.push(data.sched[currentScheduleTab].valid);
        boolArr[0] = true;	
    }
}


function deleteUser(username){
	var transaction = db.transaction(["gracefulTable"], "readwrite");
	var store = transaction.objectStore("gracefulTable");
	var req = store.delete(username);
}

function getAllUsers(arr){
	var transaction = db.transaction(["gracefulTable"], "readwrite");
	var store = transaction.objectStore("gracefulTable");
	var index = store.index("username");
	index.openCursor().onsuccess = function(evt){
        var cur = evt.target.result;
		if(cur){
			arr.push(cur.value.username);
			cur.continue();	
		}
        else{
            console.log(usernames);
            selectUser(true);
        }
	}
}


/********************************************/
// Retrieving profile information for display
/********************************************/
//Currently test data:
var userName = "";
var usernames = [];

function selectUser(done){
    loadLoginOverlay();
    if (done == false){
        getAllUsers(usernames);   
    }
    else if (done == true){
        $("#login_overlay").html("");
            
        // create all user images and selections
        
        var $userChoice, $userChoiceImage, $userChoiceLabel;
        for (var i = 0; i < usernames.length; i++){
            $userChoice = $("<div>").addClass("login_user_choice").attr("id", usernames[i]);
            $userChoiceLabel = $("<div>").addClass("login_user_choice_label").attr("id", usernames[i]);
            $userChoiceImage = $("<div>").addClass("login_user_choice_image").attr("id", usernames[i]);
            
            $userChoiceLabel.html("<a href = \"#\" id = " + usernames[i] + ">" + usernames[i] + "</a>");
            
            $userChoiceImage.css({
                "filter":"hue-rotate(" + (100 * (i + 1)) +"deg)",
                "-webkit-filter":"hue-rotate(" + (100* (i + 1)) +"deg)",
            });
            
            $userChoice.appendTo($("#login_overlay"));
            $userChoiceImage.appendTo($userChoice);
            $userChoiceLabel.appendTo($userChoice);
            
            $userChoiceImage.click(function(e){
                e.preventDefault();
                userName = $(e.target).attr("id");
                fadeLoginOverlay();
                store(userName);
                // load user schedule info
                loadUserSchedules(false);
                // load user profile info
                prepProfile();
                // load user resume
                initPDF();
                loadUserResume(userName, false);
                loadUserIdNum(userName, false);
            });
            
            $userChoiceLabel.click(function(e){
                e.preventDefault();
                userName = $(e.target).attr("id");
                fadeLoginOverlay();
                store(userName);
                // load user schedule info
                loadUserSchedules(false);
                // load user profile info
                prepProfile();
                // load user resume
                initPDF();
                loadUserResume(userName, false);
                loadUserIdNum(userName, false);
            });
        }
        
        $userChoice = $("<div>").addClass("login_user_choice");
        $userChoiceLabel = $("<div>").addClass("login_user_choice_label").attr("id", "create_new_user");
        $userChoiceImage = $("<div>").addClass("login_user_choice_image").attr("id", "new_user_image");

        $userChoiceLabel.html("New User");

        $userChoice.appendTo($("#login_overlay"));
        $userChoiceImage.appendTo($userChoice);
        $userChoiceLabel.appendTo($userChoice);

        $userChoiceImage.click(function(e){
            e.preventDefault();
            $("#login_overlay").html("");
            
            var $userChoice, $userChoiceImage, $userChoiceLabel;
            $userChoice = $("<div>").addClass("login_user_choice");
            $userChoiceLabel = $("<div>").addClass("login_user_choice_label").attr("id", "create_new_user");
            $userChoiceImage = $("<div>").addClass("login_user_choice_image").attr("id", "new_user_image");
            $userChoice.appendTo($("#login_overlay"));
            $userChoiceImage.appendTo($userChoice);
            $userChoiceLabel.appendTo($userChoice);

            
            $("#create_new_user").html(
                "<input maxlength = 25>" +
                "</input>" + 
                "<button onclick = \"javascript:createNewUser();\">" +
                "Create</button>" +
                "<button onclick = \"javascript:selectUser(true);\">" +
                "Cancel</button>"
                
            );
            
            $("#login_overlay").css({
                "left": ($("#base_page").width()/2) - ($("#login_overlay").width()/2),
            });
        });
        
        
        $("#login_overlay").css({
            "left": ($("#base_page").width()/2) - ($("#login_overlay").width()/2),
        });
        
        //$("#login_overlay_link").html();
    }
}

function createNewUser(){
    if ($.inArray($("#create_new_user input").val(), usernames) == -1){
        userName = $("#create_new_user input").val();
        store(userName);
        fadeLoginOverlay();
        // load user schedule info
        loadUserSchedules(false);
        // load user profile info
        prepProfile();
        // load user resume
        initPDF();
        loadUserResume(userName, false);
        setUserIdNum(userName, false);
    }
    else
        alert("That user name already exists.");
}


function loadUserIdNum(username, done){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        id_num = data.id_num;
    }
}

function setUserIdNum(username, done){
    $.ajax({
        type: "GET",
        url: "retrievenewid",
        success: function(data){
            id_num = parseInt(data);
            var transaction = db.transaction(["gracefulTable"], "readwrite");
            var store = transaction.objectStore("gracefulTable");
            var req = store.get(username);
            req.onsuccess = function(){
                var data = req.result;
                data.id_num = id_num;
                var update = store.put(data);
            }
        }
    });
}