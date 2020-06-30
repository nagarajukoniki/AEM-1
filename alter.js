(function (document, $) {
    'use strict';        
    
    var headLineColor = "";
    var bgcolorsmapping = {};
    $(document).on('dialog-ready', function(e) {
        var colorTheme = $(".coral-Form-fieldwrapper").find('[name="./acColorThemes"]').val();            
            if (!_.isEmpty(colorTheme)) {    
                if(colorTheme === 'white'){
					$(".coral-Form-fieldwrapper").find('[data-granite-coral-multifield-name="./linklist"]').parent().show(); 
                } else {
                    $(".coral-Form-fieldwrapper").find('[data-granite-coral-multifield-name="./linklist"]').parent().hide(); 
                }
            }
        var url= $('#alternateContentDialog').closest('form').attr('action');
        url = url+".json";
        if(url){
            $.ajax({
                url: url,
                type: "GET",
                async: false,
            }).done(function( jsonResponse ) { 
                if(jsonResponse){
                    headLineColor = jsonResponse.headLineColor;
                }
            }).fail(function( jqxhr, textStatus, error ) {
                var err = textStatus + ", " + error;
                console.log("Unable to retrieve skinny offer dialog data...");
            });
        }
        
        $.ajax({
            url: "/apps/apac/clientlibs/apac/json/alternate_content.json",
            type: "GET",
            async: false,
            
        }).done(function( response ) { 
            if(response && response.bgcolorsmapping){ 
                bgcolorsmapping = response.bgcolorsmapping
            }
        }).fail(function( jqxhr, textStatus, error ) {
            var err = textStatus + ", " + error;
            console.log("Unable to retrieve skinny offer json data..");
        });
    });
    
    $(document).on('dialog-ready', function(e) {
        setTimeout(function(e){
            var colorTheme = $(".coral-Form-fieldwrapper").find('[name="./acColorThemes"]').val(); 
            if (!_.isEmpty(colorTheme) ) { 
				if(colorTheme === 'white'){
					$(".coral-Form-fieldwrapper").find('[data-granite-coral-multifield-name="./linklist"]').parent().show(); 
                } else {
                    $(".coral-Form-fieldwrapper").find('[data-granite-coral-multifield-name="./linklist"]').parent().hide(); 
                }
                if(bgcolorsmapping){ 
                    var headlineColorJson = [];
                    for (var key in bgcolorsmapping) {
                        if(colorTheme === bgcolorsmapping[key].bgcolorvalue){
                            headlineColorJson = bgcolorsmapping[key].headlinecolor;
                        }
                    }
                    var htmltags=$("#headlineColorForm coral-taglist").html();
                    $("#headlineColorForm coral-select-item").empty();
                    $("#headlineColorForm coral-select-item")
                    .filter(function() {
                        return !this.value || $.trim(this.value).length == 0 || $.trim(this.text).length == 0;
                    })
                    .remove();                        
                    var options = '';
                    if(headLineColor && headLineColor != undefined){                            
                        for (var key in headlineColorJson) {
                            if(headLineColor === headlineColorJson[key].value){
                                options += '<coral-select-item value="' + headlineColorJson[key].value + '" selected>' + headlineColorJson[key].name + '</coral-select-item>';
                            }else{
                                options += '<coral-select-item value="' + headlineColorJson[key].value + '" >' + headlineColorJson[key].name + '</coral-select-item>'; 
                            }
                        }
                    }else{
                        for (var key in headlineColorJson) {
                            options += '<coral-select-item value="' + headlineColorJson[key].value + '" >' + headlineColorJson[key].name + '</coral-select-item>'; 
                        }
                    }
                    $("#headlineColorForm").append(options);
                    $("#headlineColorForm coral-taglist[name='./headLineColor']").html(htmltags);                   
                }                    
            };            
            
        },1000);   
        
    });       

    $(document).on("change", "#colorThemeForm", function (e) {
		var formText = $(this).attr("id");
        if(formText === 'colorThemeForm'){
            $(this).attr("disabled",true);
            var colorTheme = $(".coral-Form-fieldwrapper").find('[name="./acColorThemes"]').val();            
            if (!_.isEmpty(colorTheme)) {    
                if(colorTheme === 'white'){
					$(".coral-Form-fieldwrapper").find('[data-granite-coral-multifield-name="./linklist"]').parent().show(); 
                } else {
                    $(".coral-Form-fieldwrapper").find('[data-granite-coral-multifield-name="./linklist"]').parent().hide(); 
                }

                var headlineColorJson = [];
                for (var key in bgcolorsmapping) {
                    if(colorTheme === bgcolorsmapping[key].bgcolorvalue){
                        headlineColorJson = bgcolorsmapping[key].headlinecolor;
                    }
                }
                if(headlineColorJson){
                    $("#headlineColorForm coral-select-item").empty();
                    $("#headlineColorForm coral-select-item")
                    .filter(function() {
                        return !this.value || $.trim(this.value).length == 0 || $.trim(this.text).length == 0;
                    })
                    .remove();
                    $("#headlineColorForm coral-tag")
                    .filter(function() {
                        return this.value;
                    }).remove();
                    var opt = '';
                    for (var key in headlineColorJson) {
                        opt += '<coral-select-item value="' + headlineColorJson[key].value + '" >' + headlineColorJson[key].name + '</coral-select-item>';
                    }
                    $("#headlineColorForm").append(opt);                          
                }                                   
            };            
            $(this).removeAttr("disabled");
        }        
    });  
    
})(document, Granite.$);
