var majorDivInfo = {
    collegeName: "",
    majorName: "",
    degreeName: "",
    credApp: "",
    credReq: ""
};
var allReqCourses = [];
var tempReqCourses = [];
var majorReqNames = [];
var courseCounter = 0;
var allCoursesTaken = [];


function prepProfile(){
    // create divs and content
    var $container = $("#profile_container");
    $container.html("");
    
    majorDivInfo = {
        collegeName: "",
        majorName: "",
        degreeName: "",
        credApp: "",
        credReq: ""
    };
    
    var $fileForm = $("<div>");
    $fileForm.attr("id", "file-form");
    $fileForm.appendTo($container);
    $fileForm.html(
        "<p>Upload a new DIG</p>" +
        "<input type=\"file\" id=\"file-select\" name=\"pdffile\" />" +
        "<button type=\"button\" id=\"upload-button\" onclick = \"javascript:uploadPDF()\">Upload</button>"
    );
    
    
    var $majorDiv = $("<div>");
    $majorDiv.attr("id", "major-div");
    $majorDiv.appendTo($container);
    
    array1 = [];
    allReqCourses = [];
    allCoursesTaken = [];
    majorReqNames = [];
    
    // set content based on user info
    // get college, major
    getCollegeAndMajor("", 0);
    displayReq(array1, false);
    
}

function getCollegeAndMajor(value, done){
    if (done == 0){
        getCollege();
    }
    else if (done == 1){
        majorDivInfo.collegeName = value;
        getMajor();
    }
    else if (done == 2){
        //got both values
        majorDivInfo.majorName = value;
        
        // set content values from HERE
        $("#major-div").html(
            majorDivInfo.collegeName + "<br />" + 
            majorDivInfo.majorName + "<br />" +
            majorDivInfo.degreeName + "<br />" +
            majorDivInfo.credApp + "/" +
            majorDivInfo.credReq + " Credits"
        );
    }
    
}

function getCoursesTaken(arr, done){
    if (done == false){
        getCompletedCourses();
    }
    else if (done == true){
        allCoursesTaken = arr;
        var $takenBox, $takenTitle, $takenContent = $("<div>"), $takenCredits, $takenFiller;
        var $container = $("#profile_container");
        
        $takenFiller = $("<div>");
        $takenFiller.css({
            "height":"3vh",
            "position" : "relative",                            
        });

        $takenContent.append($takenFiller);

        $takenBox = $("<div>").addClass("req-div");
        $container.append($takenBox);
        $takenTitle = $("<div>").addClass("req-title");
        $takenContent = $("<div>").addClass("req-content");
        $takenCredits = $("<div>").addClass("req-credits");
        $takenBox.append($takenTitle);
        $takenBox.append($takenCredits);

        $takenBox.append($takenContent);

        $takenTitle.html("Courses Completed");
        $takenCredits.html(allCoursesTaken.length);


        $takenBox.css({
            "top" : (25 + (Math.floor(counter/2) * 30)) + "vh",
        });

        if(counter%2 == 1)
            $takenBox.css({"right" : "0vw"});
        
        for (var i = 0; i < allCoursesTaken.length; i++){
            $takenContent.html($takenContent.html() + allCoursesTaken[i] + "<br />");
        }
        counter++;
        var $profileEndFiller = $("<div>");
        $profileEndFiller.css({
            "height":"3vh",
            "position" : "absolute",
            "top" : (25 + (Math.ceil(counter/2) * 30)) + "vh",
            "width" : "inherit"
        });
        $container.append($profileEndFiller);
        
    }
}


var counter = 0;
function displayReq(req, done){
    if (done == false){
        getIndexForReq(userName, req);
    }
    else if (done == true){
        //$("#testsubmission").html(arr.name + "<br />" + arr.credReq + "<br />" + arr.credApp);
        // DISPLAY ALL DIVS HERE
        var $container = $("#profile_container");
        var $majorDiv = $("#major-div");
        
        // what if new user?
        if (req.length == 0){
            //center upload form
            $("#file-form").css({
                "top" : "36.5vh",
                "left" : "25vw",
            });
            //remove major info div
            $majorDiv.remove();
            $(".req-div").remove();
            return;
        }
        else{
            $("#file-form").css({
                "top" : "2vh",
                "right" : "0vw",
                "left" : "",
            });
        }
        
        var indent = "";
        var $reqBox, $reqTitle, $reqContent = $("<div>"), $reqCredits;
        counter = 0;
        for (var i = 0; i < req.length; i++){
            for (var k = 0; k < req[i].length; k++){
                if (k == 0){
                    if (i == 0){
                        //in degree req
                        //get degree req/cr/etc
                        // append w/ major/minor info
                        majorDivInfo.degreeName = req[i][k].name;
                        
                        majorDivInfo.credReq = req[i][k].credReq.substring(18, req[i][k].credReq.length);
                        majorDivInfo.credApp = req[i][k].credApp.substring(17, req[i][k].credApp.length);
                        
                        $majorDiv.html(
                            majorDivInfo.collegeName + "<br />" + 
                            majorDivInfo.majorName + "<br />" +
                            majorDivInfo.degreeName + "<br />" +
                            majorDivInfo.credApp + "/" +
                            majorDivInfo.credReq + " Credits"
                        );
                    }
                    else{
                        // in big req
                        // add box w/ info
                        $reqFiller = $("<div>");
                        $reqFiller.css({
                            "height":"3vh",
                            "position" : "relative",                            
                        });
                        
                        $reqContent.append($reqFiller);
                    
                        $reqBox = $("<div>").addClass("req-div");
                        $container.append($reqBox);
                        if (req[i][k].name.indexOf("Catalog Year:") != -1)
                            req[i][k].name = req[i][k].name.substring(0, req[i][k].name.indexOf("Catalog Year:"));
                        
                        
                        $reqTitle = $("<div>").addClass("req-title");
                        $reqContent = $("<div>").addClass("req-content");
                        $reqCredits = $("<div>").addClass("req-credits");
                        $reqBox.append($reqTitle);
                        $reqBox.append($reqCredits);
                        
                        $reqBox.append($reqContent);
                        
                        $reqTitle.html(req[i][k].name);
                        $reqCredits.html(
                            req[i][k].credApp.substring(17, req[i][k].credApp.length) + "/" +
                            req[i][k].credReq.substring(18, req[i][k].credReq.length) 
                        );
                        
                        /***************/
                        if (majorReqNames.length > 0){
                            allReqCourses[majorReqNames.length - 1] = [];
                            allReqCourses[majorReqNames.length - 1] = tempReqCourses;
                        }
                        majorReqNames.push(req[i][k].name);
                        tempReqCourses = [];
                        
                        
                        
                        $reqBox.css({
                            "top" : (25 + (Math.floor(counter/2) * 30)) + "vh",
                        });
                        
                        if(counter%2 == 1)
                            $reqBox.css({"right" : "0vw"});
                        
                        if (req[i].length == 1){
                            // big req has no add't'l info (output this)
                            $reqContent.html("Requirements unclear: refer to DIG report.");
                        }
                        else if (req[i].length == 2){
                            if (req[i][1].reqCourses.length == 1){
                                if (req[i][1].reqCourses[0].charAt(0) == " ")
                                    $reqContent.html("Requirements unclear: refer to DIG report.");
                            }
                        }
                        counter++;
                    }
                }
                else{
                    //not in big req
                    if (req[i][k].hasOwnProperty("reqCourses")){
                        // requirement object with course list
                        if (req[i][k].reqCourses.length == 0)
                            continue;
                        if (req[i][k].reqCourses[0].charAt(0) == " "){
                            // requirement info repetition; ignore
                        }
                        else{
                            // relevant info; get
                            // req[i][k].credOrClassNeeded
                            for (var j = 0; j < req[i][k].reqCourses.length; j++){
                                // append all as <p>(?)
                                // append all with special spacing(?)
                                // append all with newlines(?)
                                $reqContent.html(
                                    $reqContent.html() + 
                                    indent + 
                                    req[i][k].reqCourses[j] + 
                                    "<br />"
                                );
                                tempReqCourses.push(req[i][k].reqCourses[j]);
                            }
                        }
                    }
                    else{
                        //"choose" or "]"
                        if (req[i][k].charAt(0) == "["){
                            //start big choose
                            $reqContent.html(
                                $reqContent.html() + 
                                indent + 
                                req[i][k] + 
                                "<br />"
                            );
                            //set indent 1
                            indent += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                        }
                        else if (req[i][k].charAt(0) == "]"){
                            //end big choose
                            // reset indent
                            indent = "";
                            $reqContent.html(
                                $reqContent.html() + 
                                indent + 
                                req[i][k] + 
                                "<br />"
                            );
                            
                        }
                        else{
                            //start small choose in big choose
                            $reqContent.html(
                                $reqContent.html() + 
                                indent + 
                                req[i][k] + 
                                "<br />"
                            );
                            // add indent 1 (indent 2)
                            indent += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                        }
                    }
                }   
            }   
        } 
        allReqCourses[majorReqNames.length - 1] = [];
        allReqCourses[majorReqNames.length - 1] = tempReqCourses;
        tempReqCourses = [];
        courseCounter = 0;
        getCoursesTaken(allCoursesTaken, false);
        parseAllReqs();
        getListOfReqs();
    }
}

var courseReqObjs = [];
function parseAllReqs(){
    courseReqObjs = [];
    var tempReqObjs = [];
    alert("okhere " + majorReqNames.length + " boo " + allReqCourses.length);
    
    for (var k = 0; k < majorReqNames.length; k++){
        alert("all" + allReqCourses[k].length + " " + k);
        for (var i = 0; i < allReqCourses[k].length; i++){
            var dept, cnum, hasAt;
            if (allReqCourses[k][i].length < 3)
                continue;
            //if (allReqCourses[i].charAt(0) == " ")
            //    allReqCourses[i] = allReqCourses[i].substring(1, allReqCourses[i].length - 1);
            var values = allReqCourses[k][i].split(" ");
            dept = values[0];
            cnum = values[1];

            if (cnum.indexOf("@") > -1){
                cnum = cnum.replace("@", "");
                hasAt = "true";
            }
            else 
                hasAt = "false";

            cnum = parseFloat(cnum);

            var req = {
                dept: dept,
                cnum: cnum,
                hasAt: hasAt
            };
            tempReqObjs.push(req);
        }
        var temp = {
            "name" : majorReqNames[k],
            "reqs" : tempReqObjs
        };
        courseReqObjs.push(temp);
        alert(" ok " + k + courseReqObjs[k].name + " " + courseReqObjs[k].reqs.length);
        tempReqObjs = [];
    }
}
