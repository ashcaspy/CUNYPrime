function checkMobile() {
  var check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))check = true})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
}


// PDF GENERATION
function initPDF(){
    var printDoc = new jsPDF();
    $("#pdf_view_pane").attr("data", printDoc.output("datauristring"));
}

function generatePDF(){
    var marginTop = 0.5, marginSide = 1;
    var verticalOffset = marginTop, horizontalOffset = marginSide;
    var fontSize = 12; // size in pts
    var lineHeight = (fontSize/72);
    var printDoc = new jsPDF('p', "in", [8.5, 11]);
    var textString = "", textLines;
    var stateString = "", zipString = "";
    
    // Basic Info
    printDoc.setFont("georgia");
    fontSize = 24;
    lineHeight = (fontSize/72);
    printDoc.setFontSize(fontSize);
    
    var textWidth = printDoc.getStringUnitWidth($("#resume_name").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    var textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_name").val());
    
    printDoc.setLineWidth(0.025);
    printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
    
    verticalOffset += lineHeight;
    
    
    fontSize = 14;
    lineHeight = (fontSize/72);
    printDoc.setFontSize(fontSize);
    //textString = $("#resume_address").val() + "\n"; 
    //textLines = printDoc.splitTextToSize(textString, 6.5);
    
    if ($("#resume_state").val() != "")
        stateString = ", ";
    if ($("#resume_zip").val() != "")
        zipString = ", ";

    
    
    
    textWidth = printDoc.getStringUnitWidth($("#resume_address").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_address").val());
    
    verticalOffset += lineHeight;
    
    textWidth = printDoc.getStringUnitWidth($("#resume_city").val() + stateString + $("#resume_state").val() + zipString + $("#resume_zip").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    var textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_city").val() + stateString + $("#resume_state").val() + zipString + $("#resume_zip").val());
    
    verticalOffset += lineHeight;
    
    textWidth = printDoc.getStringUnitWidth($("#resume_phone").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_phone").val());
    
    verticalOffset += lineHeight;
    
    textWidth = printDoc.getStringUnitWidth($("#resume_email").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_email").val());
    
    verticalOffset += lineHeight;
    
    
    // EDUCATION LOOP
    if (educationCounter > 0){
        verticalOffset += lineHeight;
        fontSize = 18;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        // section title
        horizontalOffset = 1;
        printDoc.setFontStyle("bold");
        printDoc.text(horizontalOffset, verticalOffset, "Education");
        printDoc.setLineWidth(0.025);
        printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
        
        verticalOffset += lineHeight;
        verticalOffset += (lineHeight/2);
        
        fontSize = 12;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        for (var i = 0; i < educationCounter; i++){
            horizontalOffset = 1.5;
            printDoc.setFontStyle("bolditalic");
            
            // Check if new page needed
            if (verticalOffset >= 9.25){
                printDoc.addPage();
                verticalOffset = 1;
            }
            
            
            printDoc.text(horizontalOffset, verticalOffset,  $("#resume_education_where_" + i).val());
            
            if ($("#resume_education_city_" + i).val() != "" && $("#resume_education_state_" + i).val() != "")
                stateString = ", ";
            else
                stateString = "";
            
            
            textWidth = printDoc.getStringUnitWidth($("#resume_education_city_" + i).val() + stateString + $("#resume_education_state_" + i).val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
            horizontalOffset = printDoc.internal.pageSize.width - textWidth - marginSide;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_education_city_" + i).val() + stateString + $("#resume_education_state_" + i).val());
            
            horizontalOffset = 2;
            verticalOffset += lineHeight;
            verticalOffset += (lineHeight/2);
            printDoc.setFontStyle("none");
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_education_degree_" + i).val());
            
            horizontalOffset = printDoc.internal.pageSize.width / 2 + 1;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_education_year_" + i).val());
            
            verticalOffset += lineHeight;
            horizontalOffset = 2.5;
            
            var infoString = $("#resume_education_info_" + i).val().split("\n");
            if ($("#resume_education_info_" + i).val() != ""){
                verticalOffset += (lineHeight/2);
                for (var k = 0; k < infoString.length; k++){
                    
                    // Check if new page needed
                    if (verticalOffset >= 9.75){
                        printDoc.addPage();
                        verticalOffset = 1;
                    }
                    horizontalOffset = 2.25;
                    printDoc.setFillColor(0, 0, 0);
                    printDoc.circle(horizontalOffset, verticalOffset - 0.05, 0.04, 'F');
                    horizontalOffset = 2.5;
                    var splitString = printDoc.splitTextToSize(infoString[k], 8.5 - horizontalOffset - 1);
                    printDoc.text(horizontalOffset, verticalOffset, splitString);
                    verticalOffset += (lineHeight * splitString.length);
                }
            }
            verticalOffset += lineHeight;
        }
    }
    
    
    // WORK LOOP
    if (workCounter > 0){
        verticalOffset += lineHeight;
        horizontalOffset = 1;
        fontSize = 18;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        // section title
        printDoc.setFontStyle("bold");
        printDoc.text(horizontalOffset, verticalOffset, "Experience");
        printDoc.setLineWidth(0.025);
        printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
        
        verticalOffset += lineHeight;
        verticalOffset += (lineHeight/2);
            
        fontSize = 12;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        for (var i = 0; i < workCounter; i++){
            
            // Check if new page needed
            if (verticalOffset >= 9.25){
                printDoc.addPage();
                verticalOffset = 1;
            }
            horizontalOffset = 1.5;
            printDoc.setFontStyle("bolditalic");
            printDoc.text(horizontalOffset, verticalOffset,  $("#resume_work_where_" + i).val());
            
            if ($("#resume_work_city_" + i).val() != "" && $("#resume_work_state_" + i).val() != "")
                stateString = ", ";
            else
                stateString = "";
            
            
            textWidth = printDoc.getStringUnitWidth($("#resume_work_city_" + i).val() + stateString + $("#resume_work_state_" + i).val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
            horizontalOffset = printDoc.internal.pageSize.width - textWidth - marginSide;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_work_city_" + i).val() + stateString + $("#resume_work_state_" + i).val());
            
            horizontalOffset = 2;
            verticalOffset += lineHeight;
            printDoc.setFontStyle("none");
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_work_position_" + i).val());
            
            horizontalOffset = printDoc.internal.pageSize.width / 2 + 1;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_work_year_" + i).val());
            
            verticalOffset += lineHeight;
            horizontalOffset = 2.5;
            
            var infoString = $("#resume_work_info_" + i).val().split("\n");
            if ($("#resume_work_info_" + i).val() != ""){
                verticalOffset += (lineHeight/2);
                for (var k = 0; k < infoString.length; k++){
                    
                    // Check if new page needed
                    if (verticalOffset >= 9.75){
                        printDoc.addPage();
                        verticalOffset = 1;
                    }
                    horizontalOffset = 2.25;
                    printDoc.setFillColor(0, 0, 0);
                    printDoc.circle(horizontalOffset, verticalOffset - 0.05, 0.04, 'F');
                    horizontalOffset = 2.5;
                    printDoc.text(horizontalOffset, verticalOffset, infoString[k]);
                    verticalOffset += lineHeight;
                }
            }
            verticalOffset += lineHeight;
        }

    }
    
    
    // SKILL LOOP
    if (skillCounter > 0){
        verticalOffset += lineHeight;
        horizontalOffset = 1;
        fontSize = 18;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        // section title
        printDoc.setFontStyle("bold");
        printDoc.text(horizontalOffset, verticalOffset, "Skills and Interests");
        printDoc.setLineWidth(0.025);
        printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
        
        verticalOffset += lineHeight;
        verticalOffset += (lineHeight/2);
            
        fontSize = 12;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        for (var i = 0; i < skillCounter; i++){
            
            // Check if new page needed
            if (verticalOffset >= 9.25){
                printDoc.addPage();
                verticalOffset = 1;
            }
            horizontalOffset = 1.5;
            printDoc.setFontStyle("bolditalic");
            printDoc.text(horizontalOffset, verticalOffset,  $("#resume_skill_name_" + i).val());
            
            verticalOffset += lineHeight;
            horizontalOffset = 2.5;
            printDoc.setFontStyle("none");
            
            var infoString = $("#resume_skill_info_" + i).val().split("\n");
            if ($("#resume_skill_info_" + i).val() != ""){
                verticalOffset += (lineHeight/2);
                for (var k = 0; k < infoString.length; k++){
                    
                    // Check if new page needed
                    if (verticalOffset >= 9.75){
                        printDoc.addPage();
                        verticalOffset = 1;
                    }
                    horizontalOffset = 2.25;
                    printDoc.setFillColor(0, 0, 0);
                    printDoc.circle(horizontalOffset, verticalOffset - 0.05, 0.04, 'F');
                    horizontalOffset = 2.5;
                    printDoc.text(horizontalOffset, verticalOffset, infoString[k]);
                    verticalOffset += lineHeight;
                }
            }
            verticalOffset += lineHeight;
        }

    }
    $("#pdf_view_pane").attr("data", printDoc.output("datauristring"));
    $("#pdf_view_pane_backup").attr("src", printDoc.output("datauristring"));
    
    storeUserResume(userName);
}

function downloadPDF(){
    var marginTop = 0.5, marginSide = 1;
    var verticalOffset = marginTop, horizontalOffset = marginSide;
    var fontSize = 12; // size in pts
    var lineHeight = (fontSize/72);
    var printDoc = new jsPDF('p', "in", [8.5, 11]);
    var textString = "", textLines;
    var stateString = "", zipString = "";
    
    // Basic Info
    printDoc.setFont("georgia");
    fontSize = 24;
    lineHeight = (fontSize/72);
    printDoc.setFontSize(fontSize);
    
    var textWidth = printDoc.getStringUnitWidth($("#resume_name").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    var textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_name").val());
    
    printDoc.setLineWidth(0.025);
    printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
    
    verticalOffset += lineHeight;
    
    
    fontSize = 14;
    lineHeight = (fontSize/72);
    printDoc.setFontSize(fontSize);
    //textString = $("#resume_address").val() + "\n"; 
    //textLines = printDoc.splitTextToSize(textString, 6.5);
    
    if ($("#resume_state").val() != "")
        stateString = ", ";
    if ($("#resume_zip").val() != "")
        zipString = ", ";
    
    
    
    textWidth = printDoc.getStringUnitWidth($("#resume_address").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_address").val());
    
    verticalOffset += lineHeight;
    
    textWidth = printDoc.getStringUnitWidth($("#resume_city").val() + stateString + $("#resume_state").val() + zipString + $("#resume_zip").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    var textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_city").val() + stateString + $("#resume_state").val() + zipString + $("#resume_zip").val());
    
    verticalOffset += lineHeight;
    
    textWidth = printDoc.getStringUnitWidth($("#resume_phone").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_phone").val());
    
    verticalOffset += lineHeight;
    
    textWidth = printDoc.getStringUnitWidth($("#resume_email").val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
    textOffset = (printDoc.internal.pageSize.width - textWidth) / 2;
    printDoc.text(textOffset, verticalOffset, $("#resume_email").val());
    
    verticalOffset += lineHeight;
    
    
    // EDUCATION LOOP
    if (educationCounter > 0){
        verticalOffset += lineHeight;
        fontSize = 18;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        // section title
        horizontalOffset = 1;
        printDoc.setFontStyle("bold");
        printDoc.text(horizontalOffset, verticalOffset, "Education");
        printDoc.setLineWidth(0.025);
        printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
        
        verticalOffset += lineHeight;
        verticalOffset += (lineHeight/2);
        
        fontSize = 12;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        for (var i = 0; i < educationCounter; i++){
            horizontalOffset = 1.5;
            printDoc.setFontStyle("bolditalic");
            
            // Check if new page needed
            if (verticalOffset >= 9.25){
                printDoc.addPage();
                verticalOffset = 1;
            }
            
            
            printDoc.text(horizontalOffset, verticalOffset,  $("#resume_education_where_" + i).val());
            
            if ($("#resume_education_city_" + i).val() != "" && $("#resume_education_state_" + i).val() != "")
                stateString = ", ";
            else
                stateString = "";
            
            
            textWidth = printDoc.getStringUnitWidth($("#resume_education_city_" + i).val() + stateString + $("#resume_education_state_" + i).val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
            horizontalOffset = printDoc.internal.pageSize.width - textWidth - marginSide;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_education_city_" + i).val() + stateString + $("#resume_education_state_" + i).val());
            
            horizontalOffset = 2;
            verticalOffset += lineHeight;
            verticalOffset += (lineHeight/2);
            printDoc.setFontStyle("none");
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_education_degree_" + i).val());
            
            horizontalOffset = printDoc.internal.pageSize.width / 2 + 1;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_education_year_" + i).val());
            
            verticalOffset += lineHeight;
            horizontalOffset = 2.5;
            
            var infoString = $("#resume_education_info_" + i).val().split("\n");
            if ($("#resume_education_info_" + i).val() != ""){
                verticalOffset += (lineHeight/2);
                for (var k = 0; k < infoString.length; k++){
                    
                    // Check if new page needed
                    if (verticalOffset >= 9.75){
                        printDoc.addPage();
                        verticalOffset = 1;
                    }
                    horizontalOffset = 2.25;
                    printDoc.setFillColor(0, 0, 0);
                    printDoc.circle(horizontalOffset, verticalOffset - 0.05, 0.04, 'F');
                    horizontalOffset = 2.5;
                    var splitString = printDoc.splitTextToSize(infoString[k], 8.5 - horizontalOffset - 1);
                    printDoc.text(horizontalOffset, verticalOffset, splitString);
                    verticalOffset += (lineHeight * splitString.length);
                }
            }
            verticalOffset += lineHeight;
        }
    }
    
    
    // WORK LOOP
    if (workCounter > 0){
        verticalOffset += lineHeight;
        horizontalOffset = 1;
        fontSize = 18;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        // section title
        printDoc.setFontStyle("bold");
        printDoc.text(horizontalOffset, verticalOffset, "Experience");
        printDoc.setLineWidth(0.025);
        printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
        
        verticalOffset += lineHeight;
        verticalOffset += (lineHeight/2);
            
        fontSize = 12;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        for (var i = 0; i < workCounter; i++){
            
            // Check if new page needed
            if (verticalOffset >= 9.25){
                printDoc.addPage();
                verticalOffset = 1;
            }
            horizontalOffset = 1.5;
            printDoc.setFontStyle("bolditalic");
            printDoc.text(horizontalOffset, verticalOffset,  $("#resume_work_where_" + i).val());
            
            if ($("#resume_work_city_" + i).val() != "" && $("#resume_work_state_" + i).val() != "")
                stateString = ", ";
            else
                stateString = "";
            
            
            textWidth = printDoc.getStringUnitWidth($("#resume_work_city_" + i).val() + stateString + $("#resume_work_state_" + i).val()) * printDoc.internal.getFontSize() / printDoc.internal.scaleFactor;
            horizontalOffset = printDoc.internal.pageSize.width - textWidth - marginSide;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_work_city_" + i).val() + stateString + $("#resume_work_state_" + i).val());
            
            horizontalOffset = 2;
            verticalOffset += lineHeight;
            printDoc.setFontStyle("none");
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_work_position_" + i).val());
            
            horizontalOffset = printDoc.internal.pageSize.width / 2 + 1;
            printDoc.text(horizontalOffset, verticalOffset, $("#resume_work_year_" + i).val());
            
            verticalOffset += lineHeight;
            horizontalOffset = 2.5;
            
            var infoString = $("#resume_work_info_" + i).val().split("\n");
            if ($("#resume_work_info_" + i).val() != ""){
                verticalOffset += (lineHeight/2);
                for (var k = 0; k < infoString.length; k++){
                    
                    // Check if new page needed
                    if (verticalOffset >= 9.75){
                        printDoc.addPage();
                        verticalOffset = 1;
                    }
                    horizontalOffset = 2.25;
                    printDoc.setFillColor(0, 0, 0);
                    printDoc.circle(horizontalOffset, verticalOffset - 0.05, 0.04, 'F');
                    horizontalOffset = 2.5;
                    printDoc.text(horizontalOffset, verticalOffset, infoString[k]);
                    verticalOffset += lineHeight;
                }
            }
            verticalOffset += lineHeight;
        }

    }
    
    
    // SKILL LOOP
    if (skillCounter > 0){
        verticalOffset += lineHeight;
        horizontalOffset = 1;
        fontSize = 18;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        // section title
        printDoc.setFontStyle("bold");
        printDoc.text(horizontalOffset, verticalOffset, "Skills and Interests");
        printDoc.setLineWidth(0.025);
        printDoc.line(horizontalOffset, verticalOffset + lineHeight/3, 8.5 - horizontalOffset, verticalOffset + lineHeight/3);
        
        verticalOffset += lineHeight;
        verticalOffset += (lineHeight/2);
            
        fontSize = 12;
        lineHeight = fontSize / 72;
        printDoc.setFontSize(fontSize);

        for (var i = 0; i < skillCounter; i++){
            
            // Check if new page needed
            if (verticalOffset >= 9.25){
                printDoc.addPage();
                verticalOffset = 1;
            }
            horizontalOffset = 1.5;
            printDoc.setFontStyle("bolditalic");
            printDoc.text(horizontalOffset, verticalOffset,  $("#resume_skill_name_" + i).val());
            
            verticalOffset += lineHeight;
            horizontalOffset = 2.5;
            printDoc.setFontStyle("none");
            
            var infoString = $("#resume_skill_info_" + i).val().split("\n");
            if ($("#resume_skill_info_" + i).val() != ""){
                verticalOffset += (lineHeight/2);
                for (var k = 0; k < infoString.length; k++){
                    
                    // Check if new page needed
                    if (verticalOffset >= 9.75){
                        printDoc.addPage();
                        verticalOffset = 1;
                    }
                    horizontalOffset = 2.25;
                    printDoc.setFillColor(0, 0, 0);
                    printDoc.circle(horizontalOffset, verticalOffset - 0.05, 0.04, 'F');
                    horizontalOffset = 2.5;
                    printDoc.text(horizontalOffset, verticalOffset, infoString[k]);
                    verticalOffset += lineHeight;
                }
            }
            verticalOffset += lineHeight;
        }

    }
    printDoc.save("MyResume.pdf");
}


// LOCAL STORAGE OF RESUME
function storeUserResume(username){
    userResume = {
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
    userResume.name = $("#resume_name").val();
    userResume.address = $("#resume_address").val();
    userResume.city = $("#resume_city").val();
    userResume.state = $("#resume_state").val();
    userResume.zip = $("#resume_zip").val();
    userResume.phone = $("#resume_phone").val();
    userResume.email = $("#resume_email").val();
    
    for (var i = 0; i < educationCounter; i++){        
        var temp = {
            name: $("#resume_education_where_" + i).val(),
            city: $("#resume_education_city_" + i).val(),
            state: $("#resume_education_state_" + i).val(),
            year: $("#resume_education_year_" + i).val(),
            degree: $("#resume_education_degree_" + i).val(),
            info: $("#resume_education_info_" + i).val(),
        };
        userResume.education.push(temp);
    }
    
    for (var i = 0; i < workCounter; i++){        
        var temp = {
            name: $("#resume_work_where_" + i).val(),
            city: $("#resume_work_city_" + i).val(),
            state: $("#resume_work_state_" + i).val(),
            year: $("#resume_work_year_" + i).val(),
            position: $("#resume_work_position_" + i).val(),
            info: $("#resume_work_info_" + i).val(),
        };
        userResume.work.push(temp);
    }
    
    for (var i = 0; i < skillCounter; i++){        
        var temp = {
            name: $("#resume_skill_name_" + i).val(),
            info: $("#resume_skill_info_" + i).val(),
        };
        userResume.skills.push(temp);
    }
    
    var transaction = db.transaction(["gracefulTable"], "readwrite");
    var store = transaction.objectStore("gracefulTable");
    var request = store.get(username);
    request.onsuccess = function(){
        var data = request.result;
         data.resume = userResume;
         var update = store.put(data);
    }
}

function loadUserResume(username, flag){
    if (flag == false){
        console.log("false");
        
        var transaction = db.transaction(["gracefulTable"], "readwrite");
        var store = transaction.objectStore("gracefulTable");
        var req = store.get(username);
        req.onsuccess  = function(){
            userResume = req.result.resume;
            loadUserResume(username, true);
        }
    }
    else if (flag == true){        
        console.log("true");
        
        $("#resume_name").val(userResume.name);
        $("#resume_address").val(userResume.address);
        $("#resume_city").val(userResume.city);
        $("#resume_state").val(userResume.state);
        $("#resume_zip").val(userResume.zip);
        $("#resume_phone").val(userResume.phone);
        $("#resume_email").val(userResume.email);
        
        for (var i = 0; i < userResume.education.length; i++){
            addEducation();
            $("#resume_education_where_" + i).val(userResume.education[i].name);
            $("#resume_education_city_" + i).val(userResume.education[i].city);
            $("#resume_education_state_" + i).val(userResume.education[i].state);
            $("#resume_education_year_" + i).val(userResume.education[i].year);
            $("#resume_education_degree_" + i).val(userResume.education[i].degree);
            $("#resume_education_info_" + i).val(userResume.education[i].info);
        }
        for (var i = 0; i < userResume.work.length; i++){
            addWork();
            $("#resume_work_where_" + i).val(userResume.work[i].name);
            $("#resume_work_city_" + i).val(userResume.work[i].city);
            $("#resume_work_state_" + i).val(userResume.work[i].state);
            $("#resume_work_year_" + i).val(userResume.work[i].year);
            $("#resume_work_position_" + i).val(userResume.work[i].position);
            $("#resume_work_info_" + i).val(userResume.work[i].info);
        }
        
        for (var i = 0; i < userResume.skills.length; i++){
            addSkill();
            $("#resume_skill_name_" + i).val(userResume.skills[i].name);
            $("#resume_skill_info_" + i).val(userResume.skills[i].info);
        }
        generatePDF();
    }
}

// ADD NEW EDUCATION/WORK/SKILL
var workCounter = 0;
var educationCounter = 0;
var skillCounter = 0;

function addEducation(){
    var $newEducation = $("<div>").addClass("resume_input_div");
    $newEducation.attr("id", "resume_education_" + educationCounter);
    $newEducation.html(
        "<b>College Name:<b><input id = \"resume_education_where_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" + 
        "<b>City:</b><input id = \"resume_education_city_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" + 
        "<b>State:</b><input id = \"resume_education_state_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" + 
        "<b>Degree:</b><input id = \"resume_education_degree_" + educationCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Graduation Year:</b><input id = \"resume_education_year_" + educationCounter + "\" maxlength = 50 placeholder = \"Month, Year\" onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Additional Info:</b><br /><textarea id = \"resume_education_info_" + educationCounter + "\" maxlength = 1000 rows = 4 placeholder = \"Enter a list of achievements, separated by newlines.\" onblur = \"javascript:generatePDF();\"></textarea><br /><br /><br /><br />"
    );
    
    $("#resume_education").append($("<div>").addClass("resume_filler").html("<b>Education " + (educationCounter+1) + "</b><div class = \"resume_remove_section\"><a href = \"#\" class = \"resume_remove_section_link\" onclick = \"javascript:removeEducation(" + educationCounter + ");\"></a></div>").attr("id", "resume_education_filler_" + educationCounter));
    $("#resume_education").append($newEducation);
    educationCounter++;
    if (checkMobile() == true){
        $(".resume_input_div br").css({"line-height":"200%"});
    }
    
}

function addWork(){
    var $newWork = $("<div>").addClass("resume_input_div");
    $newWork.attr("id", "resume_work_" + workCounter);
    $newWork.html(
        "<b>Employer:</b><input id = \"resume_work_where_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>City:</b><input id = \"resume_work_city_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>State:</b><input id = \"resume_work_state_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Position:</b><input id = \"resume_work_position_" + workCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Dates:</b><input id = \"resume_work_year_" + workCounter + "\" maxlength = 50 placeholder = \"Start - End\" onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Responsibilities:</b><br /><textarea id = \"resume_work_info_" + workCounter + "\" maxlength = 1000 rows = 4 placeholder = \"Enter a list of responsibilities, separated by newlines.\" onblur = \"javascript:generatePDF();\"></textarea><br /><br /><br /><br />"
    );
    
    $("#resume_work").append($("<div>").addClass("resume_filler").html("<b>Work Experience " + (workCounter+1) + "</b><div class = \"resume_remove_section\"><a href = \"#\" class = \"resume_remove_section_link\" id = \"testid\" onclick = \"javascript:removeWork(" + workCounter + ");\"></a></div>").attr("id", "resume_work_filler_" + workCounter));
    $("#resume_work").append($newWork);
    workCounter++;
    if (checkMobile() == true){
        $(".resume_input_div br").css({"line-height":"200%"});
    }
}


function addSkill(){
    var $newSkill = $("<div>").addClass("resume_input_div");
    $newSkill.attr("id", "resume_skill_" + skillCounter);
    $newSkill.html(
        "<b>Skill Name:</b><input id = \"resume_skill_name_" + skillCounter + "\" maxlength = 50 onblur = \"javascript:generatePDF();\"></input><br />" +
        "<b>Description:</b><br /><textarea id = \"resume_skill_info_" + skillCounter + "\" maxlength = 1000 rows = 4 onblur = \"javascript:generatePDF();\"></textarea><br /><br /><br /><br />"
    );
    
    $("#resume_skills").append($("<div>").addClass("resume_filler").html("<b>Skill/Interest " + (skillCounter+1) + "</b><div class = \"resume_remove_section\"><a href = \"#\" class = \"resume_remove_section_link\" id = \"testid\" onclick = \"javascript:removeSkill(" + skillCounter + ");\"></a></div>").attr("id", "resume_skill_filler_" + skillCounter));
    $("#resume_skills").append($newSkill);
    skillCounter++;

    if (checkMobile() == true){
        $(".resume_input_div br").css({"line-height":"200%"});
    }
}



// REMOVE SELECTED EDUCATION/WORK/SKILL
function removeEducation(num){
    $("#resume_education_" + num).remove();
    $("#resume_education_filler_" + num).remove();
    for (var i = num; i < educationCounter-1; i++){
        $("#resume_education_" + (i+1)).attr("id", "resume_education_" + i);
        $("#resume_education_filler_" + (i+1)).attr("id", "resume_education_filler_" + i);
        
        $("#resume_education_filler_" + i).html("<b>Education " + (i+1) + "</b><div class = \"resume_remove_section\"><a href = \"#\" class = \"resume_remove_section_link\" onclick = \"javascript:removeEducation(" + i + ");\"></a></div>");
    }
    
    educationCounter--;
    if (checkMobile() == true){
        $(".resume_input_div br").css({"line-height":"200%"});
    }
}


function removeWork(num){
    $("#resume_work_" + num).remove();
    $("#resume_work_filler_" + num).remove();
    for (var i = num; i < workCounter-1; i++){
        $("#resume_work_" + (i+1)).attr("id", "resume_work_" + i);
        $("#resume_work_filler_" + (i+1)).attr("id", "resume_work_filler_" + i);
        
        $("#resume_work_filler_" + i).html("<b>Work Experience " + (i+1) + "</b><div class = \"resume_remove_section\"><a href = \"#\" class = \"resume_remove_section_link\" onclick = \"javascript:removeWork(" + i + ");\"></a></div>");
    }
    
    workCounter--;

    if (checkMobile() == true){
        $(".resume_input_div br").css({"line-height":"200%"});
    }
}

function removeSkill(num){
    $("#resume_skill_" + num).remove();
    $("#resume_skill_filler_" + num).remove();
    for (var i = num; i < skillCounter-1; i++){
        $("#resume_skill_" + (i+1)).attr("id", "resume_skill_" + i);
        $("#resume_skill_filler_" + (i+1)).attr("id", "resume_skill_filler_" + i);
        
        $("#resume_skill_filler_" + i).html("<b>Skill/Interest " + (i+1) + "</b><div class = \"resume_remove_section\"><a href = \"#\" class = \"resume_remove_section_link\" onclick = \"javascript:removeSkill(" + i + ");\"></a></div>");
    }
    
    skillCounter--;
    if (checkMobile() == true){
        $(".resume_input_div br").css({"line-height":"200%"});
    }
}
