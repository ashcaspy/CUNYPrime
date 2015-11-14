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

function globalDbErrorHandler(event){
    window.alert("Error has occurred " + event.target.errorCode);
}

function createDbObject(evt){
	var options = {keyPath: "username"};
	var storage = evt.createObjectStore("gracefulTable", options);
	storage.createIndex("username", "username", {unique: true});
}

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
function store(userName){
    

    var input = {
        username: userName,
        college: collegeOfStudent, 
        major: major, 
        //courses: requirements, 
        coursesInProgress: coursesInProgress,
        coursesTaken: coursesTaken,
        coursesTransfer: coursesTransfer,
        coursesWithdrew: coursesWithdrew,
        coursesNC: coursesNotCompleted,
        sched: schedArr,
        allCourseReq: allCourseReq
    }

    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");

    var request = store.add(input);

    request.onsuccess = function(){
        //window.alert("done adding");
    }
    request.onerror = function(){
        var transaction = db.transaction(["gracefulTable"], "readwrite");
        var store = transaction.objectStore("gracefulTable");
        var request2 = store.get(username);
        request2.onsuccess = function(){
            alert("Username " + username + " is already taken. Please choose another one.");
        }
    }
}


function getIndexForReq(username, arr){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess  = function(){
        var data = req.result;
        
        //if(index < 0 || index > data.req.length){
        //    console.log("out of bound");
        //} else {
            /*for (var i = 0; i < data.req[index].length; i++){
                arr.push(data.req[index][i]);
                
            }*/
            arr = data.req;
            displayReq(arr, true);
        //}

        console.log(arr);
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


var db;
window.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

if (window.indexedDB){
    var request = window.indexedDB.open("GracefulDb", 8);
    request.onerror = function(event){
        window.alert("Error creating database");
    };
    request.onsuccess = function(event){
        db = request.result;

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
            
            var outputString = data.replace(/(\r\n|\n|\r)/gm, "\n");
            
            populateCourseRequirements(outputString);
            
            var outputString = data.replace(/(\r\n|\n|\r)/gm, "<br>");
            $("#testsubmission").html(outputString);
            storeReq(userName);
            //getIndexForReq(userName, 0, array1); 
            displayReq(array1, 0, false);
            //alert(array1[0].name);
        }
            
    });
}



/*****************************************************/
// SCHEDULE GETS AND SETS
/*****************************************************/

	
function Schedule(dayStart, dayEnd, hoursStart, hoursEnd, openTimes, closeTimes, selectedDiv, valid, classTimes, selectedCourses){
    this.dayStart = dayStart;
    this.dayEnd = dayEnd;
    this.hoursStart = hoursStart;
    this.hoursEnd = hoursEnd;
    this.openTimes = openTimes;
    this.closeTimes = closeTimes;
    this.selectedDiv = selectedDiv;
    this.valid = valid;
    this.classTimes = classTimes;
    this.selectedCourses = selectedCourses;
}


//FUNCTIONS FOR SCHEDULES 
//IT SHOULD GO INTO THE PROFILEFUNCTIONS.JS SO THE VARIABLE DB IS KNOWN!

function createSched(username){
    var open = [];
    var closedTime = [];
    var selectedDiv = [];
    var classTime = [];
    var selectedCourse = [];
    var testSched = new Schedule(0,6,0,23,open, closedTime, selectedDiv, true, classTime, selectedCourse);
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
        data.sched[currentScheduleTab].hoursStart = hoursStart;
        var update = store.put(data);
    }
}

function setHoursEnd(username, hoursEnd) {
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].hoursEnd = hoursEnd;
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
        data.sched[currentScheduleTab].closeTimes = closeTimes;
        var update = store.put(data);
    }
}

function setSelectedDiv(username, selectedDiv){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].selectedDiv = selectedDiv;
        var update = store.put(data);
    }
}

function setSelectedCourses(username, selectedDiv){
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var req = store.get(username);
    req.onsuccess = function(){
        var data = req.result;
        data.sched[currentScheduleTab].selectedCourses = selectedCourses;
        var update = store.put(data);
    }
}

function setClassTimes(username, selectedDiv){
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

        for(var i = 0; i < data.sched[currentScheduleTab].closeTimes.length; i++){
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

        for(var i = 0; i < data.sched[currentScheduleTab].selectedDiv.length; i++){
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

        for(var i = 0; i < data.sched.length; i++){
            myArr.push(data.sched[i]);
        }

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

	}
}


/********************************************/
// Retrieving profile information for display
/********************************************/
//Currently test data:
var userName = "";

function selectUser(){
    loadLoginOverlay();
    
    $("#login_overlay_link").click(function(e){
        e.preventDefault();
        userName = "user";
        console.log("username set");
        fadeLoginOverlay();
        store(userName);
        
        // load user schedule info
        loadUserSchedules(false);

    });
}




