(function(){

$.fn.popbox = function(options){
        var settings = $.extend({
        selector      : this.selector,
        open          : '.open',
        box           : '.box',
        arrow         : '.arrow',
        arrow_border  : '.arrow-border',
        close         : '.close',
        course_info   : '.course_info'
    }, options);

    var timeoutId1;
    var timeoutId2;
    var currentMousePos = {x: -1, y: -1};
    
        $(document).mousemove(function(event)
    {
        currentMousePos.x = event.pageX;
        currentMousePos.y = event.pageY;
    });
    var position = $(this).offset();
    var methods = {

        close: function(){
            clearTimeout(timeoutId2);
            clearTimeout(timeoutId1);
            timeoutId2 = setTimeout(function(){
                $(settings['box']).fadeOut("fast");
            }, 250);
        // document.getElementById("footer").innerHTML = inPop + "1";
        },


        open: function(event){
            //if (popOpened == 1)
            //    methods.close();
            event.preventDefault();
            $this = $(this);

            var id = $this.attr('id');
            var list_num = parseInt(id.charAt(4), 10);

            var result_num = parseInt(id.substr(8));
            //document.getElementById("footer").innerHTML = id;

            var pop = $this;
            var box = $this.parents("#page").find("#course_info_" + list_num).find("#result" + result_num).find(settings['box']);


            clearTimeout(timeoutId1);  
            timeoutId1 = setTimeout(function() {
                box.find(settings['arrow']).css({'left': box.width()/2 + 20});
                box.find(settings['arrow_border']).css({'left': box.width()/2 - 10});

                //if(box.css('display') == 'block'){
                //  methods.close();
                //} else {
                $(settings[box]).fadeIn(500);
                box.css({'display': 'block', 'top': Math.floor(pop.offset().top + pop.height()*4/3), 'left': Math.floor(pop.offset().left) - pop.width()/2});

                //}

            }, 500);

            //document.getElementById("footer").innerHTML = popOpened + "in O";
        }

    };

    $(document).bind('keyup', function(event){
        if(event.keyCode == 27){
            methods.close();
        }
    });
        
    $(settings['open'], this).bind('mouseout', function(event){
        //if(!$(event.target).closest(settings['selector']).length){      
        methods.close();
        
    });
    
    $(settings['course_info']).bind('mouseenter', function(event){
        clearTimeout(timeoutId1);
        clearTimeout(timeoutId2);
        
    });

    $(settings['course_info']).bind('mouseout', function(event){
        var e = event.toElement || event.relatedTarget;

        //check for all children levels (checking from bottom up)
        while(e && e.parentNode && e.parentNode != window) {
            if (e.parentNode == this||  e == this) {
                if(e.preventDefault) e.preventDefault();
                return false;
            }
            e = e.parentNode;
        }
        methods.close();
    });
   
   
    
    

    return this.each(function(){
        $(this).css({'width': $(settings['box']).width()}); // Width needs to be set otherwise popbox will not move when window resized.
        $(settings['open'], this).bind('mouseenter', methods.open);
        /*    $(settings['open'], this).bind('mouseenter', function(event){
        if (popOpened == 0)
        methods.open;
        });
        */  


        /*$(settings['open'], this).parent().find(settings['close']).bind('mouseout', function(event){
        event.preventDefault();
        methods.close();
        });*/
    });
}

}).call(this);
