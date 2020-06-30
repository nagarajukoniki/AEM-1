$(document).on("change", ".coral3-Select", function (e) { 
	validateImageType();
    hideLinkAndDisplayPopUp();
});
$(document).on('dialog-ready', function(e) {
     setTimeout(function(e){
    validateImageType();
    hideLinkAndDisplayPopUp();
    $(".coral-Form-fieldwrapper").find('[name="./isDecorative"]').change(function () {
        hideLinkOnChange();
    });
    if($("coral-fileupload .cq-dd-image").attr('src')!= ""){
        hideLinkOnChange();
    }         
   },1000);   
});

function hideLinkOnChange() {
	$(".coral-Form-fieldwrapper").find('[name="./linkURL"]').parent().hide();
    $(".cmp-image__editor-link").children().first().css("display","none");
    $(".cmp-image__editor-link  coral-icon").hide();
}
function hideLinkAndDisplayPopUp(){
	$(".coral-Form-fieldwrapper").find('[name="./displayPopupTitle"]').parent().hide();
    $(".cmp-image__editor-link").hide();
}
function validateImageType(){
	var imageType =  $(".coral3-Select").find('[name="./imagetype"]').val();
	if(imageType == "hero") {
		$(".coral-Well").find('[name="./jcr:title"]').parent().parent().hide();
	} else {
		$(".coral-Well").find('[name="./jcr:title"]').parent().parent().show();
	}
}
