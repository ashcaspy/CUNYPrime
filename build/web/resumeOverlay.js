// PDF GENERATION
function initPDF(){
    var printDoc = new jsPDF();
    $("#pdf_view_pane").attr("data", printDoc.output("datauristring"));
}

function generatePDF(){
    var printDoc = new jsPDF();
    //printDoc.fromHTML($('#display_pane').get(0), 10, 10, {'width': 180});
    //printDoc.autoPrint();
    printDoc.text(20, 20, $("#resume_name").val());
    printDoc.text(20, 30, $("#resume_address").val());
    var stateString = "";
    if ($("#resume_state").val() != "")
        stateString = ", ";
    
    printDoc.text(20, 40, $("#resume_city").val() + stateString+ $("#resume_state").val());
    printDoc.text(20, 50, $("#resume_zip").val());
    $("#pdf_view_pane").attr("data", printDoc.output("datauristring"));
    
}


// ADD NEW EDUCATION/WORK
var workCounter = 0;
var educationCounter = 0;
function addEducation(){
    var $newEducation = $("<div>").addClass("resume_input_div");
    $newEducation.attr("id", "resume_education_" + educationCounter);
    $newEducation.html(
        "College Name:<input id = \"resume_education_where_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" + 
        "Graduation Year:<input id = \"resume_education_year_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "Degree:<input id = \"resume_education_degree_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "Additional Info:<input id = \"resume_education_info_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />"
    );
    
    $("#resume_education").append($("<div>").addClass("resume_filler").html("<b>Education " + (educationCounter+1) + ":</b>"));
    $("#resume_education").append($newEducation);
    educationCounter++;
    
}

function addWork(){
    var $newWork = $("<div>").addClass("resume_input_div");
    $newWork.attr("id", "resume_education_" + workCounter);
    $newWork.html(
        "<b>Employer:</b><input id = \"resume_work_where_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Dates:</b><input id = \"resume_work_year_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Position:</b><input id = \"resume_work_position_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Responsibilities:</b><input id = \"resume_work_info_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />"
    );
    
    $("#resume_work").append($("<div>").addClass("resume_filler").html("<b>Work Experience " + (workCounter+1) + ":</b>"));
    $("#resume_work").append($newWork);
    workCounter++;
}


// EXPAND AND COLLAPSE DIVS (MAYBE ABANDON THIS)

